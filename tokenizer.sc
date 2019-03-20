
import scala.io.Source
import java.io._
import java.util.Calendar
import scala.collection.mutable.LinkedHashMap
import edu.holycross.shot.scm._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.seqcomp._
import edu.furman.classics.citewriter._
import scala.io.StdIn.readLine
import scala.collection.mutable._


val filePath:String = "cex/" 
val splitters:String = """[\[\])(·⸁.,·;;   "?·!–—⸂⸃]"""

def splitWithSplitter(text:String, splitters:String = """[\[\]··⸁.; "?!–—⸂⸃]"""):Vector[String] = {
	val regexWithSplitter = s"(?<=${splitters})"
	text.split(regexWithSplitter).toVector.filter(_.size > 0)
}

def tokenizeCtsNode(node:CitableNode, splitters:String = splitters, exemplarID:String = "token"):Vector[CitableNode] = {
	try {

		val editionUrn:CtsUrn = node.urn.dropPassage
		// Check that the URN is at the Version level
		if ( editionUrn.exemplarOption != None) throw new Exception(s"The text cannot already be an exemplar! (${editionUrn})")
		// If we get here, we're fine
		val exemplarUrn:CtsUrn = editionUrn.addExemplar(exemplarID)
		val editionCitation:String = node.urn.passageComponent
		val passage:String = node.text
		//val tokens:Vector[String] = splitWithSplitter(passage, splitters)
		val tokens:Vector[String] = splitWithSplitter(passage).toVector.filter(_.size > 0)
		val tokenizedNodes:Vector[CitableNode] = {
			tokens.zipWithIndex.map{ case (n, i) => {
				val newUrn:CtsUrn = CtsUrn(s"${exemplarUrn}${editionCitation}.${i}")
				val newNode:CitableNode = CitableNode(newUrn, n)
				newNode
			}}.filter(_.text != " ").filter(_.text.size > 0).toVector
		}
		tokenizedNodes
	} catch {
		case e:Exception => throw new Exception(s"${e}")
	}
}

def deTokenize(c:Corpus):Vector[CitableNode] = {
	import scala.collection.mutable.LinkedHashMap
	val v1:Vector[CitableNode] = c.nodes
	val v2 = v1.zipWithIndex.groupBy( n => n._1.urn.collapsePassageBy(1))
	val v3 = LinkedHashMap(v2.toSeq sortBy (_._2.head._2): _*)
	val v4 = v3 mapValues (_ map (_._1))
	val lineVec:Vector[(CtsUrn, scala.collection.immutable.Vector[CitableNode])] = v4.toVector

	val lineNodes:Vector[CitableNode] = lineVec.map( lv => {
		val u:CtsUrn = lv._1
		val tokVec:Vector[CitableNode] = lv._2
		val tokString:String = tokVec.map(_.text.replaceAll(" ","_")).mkString(" ")
		println(s"${u}")
		CitableNode(u, tokString)
	})
	lineNodes
	/*
	val urns:Vector[CtsUrn] = {
		c.urns.map( _.collapsePassageBy(1)).distinct
	}
	val passages:Vector[CitableNode] = {
		urns.map( u => {
			println(s"${u.passageComponent}")
			val passageCorp = c ~~ u
			val toks:Vector[String] = passageCorp.nodes.map(_.text)
			val tokenLine:String = toks.mkString(" ")
			println(s"\t${tokenLine}")
			CitableNode(u, tokenLine)
		})
	}
	passages
	*/
}

def tokenizeCorpus(c:Corpus, splitters:String = splitters, exemplarID:String = "token"):Corpus = {
	val nodeVector:Vector[CitableNode] = c.nodes.map( n => tokenizeCtsNode(n, splitters, exemplarID)).flatten
	val newCorpus:Corpus = Corpus(nodeVector)
	newCorpus
}


def loadLibrary(fp:String = "/Users/cblackwell/Desktop/vm_Spring_2019/workspace2019/cex/hmt-2018g.cex"):CiteLibrary = {
	val library = CiteLibrary(Source.fromFile(fp).getLines.mkString("\n"),"#",",")
	library
}

def loadFile(fp:String = "../iliad_alignment/iliad_pope.txt"):Vector[String] = {
	Source.fromFile(fp).getLines.toVector
}


def saveString(s:String, filePath:String = filePath, fileName:String = "temp.txt"):Unit = {
	val pw = new PrintWriter(new File(filePath + fileName))
	for (line <- s.lines){
		pw.append(line)
		pw.append("\n")
	}
	pw.close
}

def showMe(v:Any):Unit = {
	v match {
		case _:Iterable[Any] => println(s"""----\n${v.asInstanceOf[Iterable[Any]].mkString("\n")}\n----""")
		case _ => println(s"-----\n${v}\n----")
	}
}

// Convert an Int to a Roman Numeral
def toRoman(value: Int): String = {
"M" * (value / 1000) +
  ("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM").productElement(value % 1000 / 100) +
  ("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC").productElement(value % 100 / 10) +
  ("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX").productElement(value % 10)
}

// Convert a String that is a Roman Numeral to an Int
def fromRoman(s: String) : Int = {
	try {
		val numerals = Map('I' -> 1, 'V' -> 5, 'X' -> 10, 'L' -> 50, 'C' -> 100, 'D' -> 500, 'M' -> 1000)

		s.toUpperCase.map(numerals).foldLeft((0,0)) {
		  case ((sum, last), curr) =>  (sum + curr + (if (last < curr) -2*last else 0), curr) }._1
	} catch {
		case e:Exception => throw new Exception(s""" "${s}" is not a valid Roman Numeral.""")
	}
}

def tokenizedCatEntry(versionCat:CatalogEntry, exemplarId:String = "token"):CatalogEntry = {
	val urn:CtsUrn = versionCat.urn.addExemplar(exemplarId)
	val citationScheme:String = s"${versionCat.citationScheme}/token"
	val lang:String = versionCat.lang
	val groupName:String = versionCat.groupName
	val workTitle:String = versionCat.workTitle
	val versionLabel:Option[String] = versionCat.versionLabel
	val exemplarLabel:Option[String] = Some("Tokenized")
	val online:Boolean = true

	CatalogEntry(urn, citationScheme, lang, groupName, workTitle, versionLabel, exemplarLabel, online)
}



/* Task-specific stuff below! */

val libraryFile:String = "cex/library.cex"

// For CEX Generation
val defaultExemplarId:String = "token"
val delim1:String = "#"
val delim2:String = ","
val defaultFileNameBase:String = "cex_export"

lazy val lib:CiteLibrary = loadLibrary(libraryFile)

lazy val tr:TextRepository = lib.textRepository.get

lazy val corp:Corpus = tr.corpus

lazy val libCex:String = CexWriter.writeCiteLibrary(
        lib.textRepository,
        lib.collectionRepository,
        lib.relationSet,
        lib.dataModels,
        true,
        "#",
        ","
    )

lazy val works:Vector[edu.holycross.shot.ohco2.LabelledCtsUrn] = tr.catalog.labelledWorks.toVector

lazy val versions:Vector[CatalogEntry] = tr.catalog.texts.toVector


def listWorks:String = {
	works.zipWithIndex.map( tup => {
		s"w${tup._2}. ${tup._1.label}: ${tup._1.urn}\n"
	}).mkString(s"\n")
}

def listVersions:String = {
	versions.zipWithIndex.map( tup => {
		s"v${tup._2}.  ${tup._1.fullLabel}\n"
	}).mkString(s"\n")
}

var textsToTokenize:ListBuffer[CtsUrn] = new ListBuffer[CtsUrn]
var textsToInclude:ListBuffer[CtsUrn] = new ListBuffer[CtsUrn]



def addText(t:String, p:String = "", tokenize:Boolean = true):Unit = {
	t.toVector.head match {
		case 'w' => {
			val i:Int = t.toVector.tail.head.toString.toInt
			val u:CtsUrn = works(i).urn
			val urn:CtsUrn = CtsUrn(s"${u}${p}")
			if (tokenize) {
				textsToTokenize += urn
			} else {
				textsToInclude += urn
			}
			println(s"\nTexts to tokenize:")
			showMe(textsToTokenize)
			println(s"\nTexts to include:")
			showMe(textsToInclude)
		}
		case 'v' => {
			val i:Int = t.toVector.tail.head.toString.toInt
			val u:CtsUrn = versions(i).urn
			val urn:CtsUrn = CtsUrn(s"${u}${p}")
			if (tokenize) {
				textsToTokenize += urn
			} else {
				textsToInclude += urn
			}
			println(s"\nTexts to tokenize:")
			showMe(textsToTokenize)
			println(s"\nTexts to include:")
			showMe(textsToInclude)
		}
		case _ => {
			println(s"The version- or work-ID must begin with 'v' or 'w'.")
		}
	}	
}

def clearAll:Unit = { textsToTokenize.clear; textsToInclude.clear }

def instructions:String = """

Add files to your library with, e.g.:

	> addText("w0")

	(all versions of a notional work)

	> addText("v1")

	(a single version of a notional work)

Constrain to specific ranges by adding a citation, e.g.:

	> addText("v0", "2.1-2.10")

By default, this will produce a tokenized version of
your text. To include only a selection (a version, 
some passages) without tokenization, add "tokenize=false":

	> addText("v0", "2.1-2.10", tokenize=false)

See list of works and versions with: 

	> menu

Clear the list of texts-to-be-written with:

	> clearAll

Write out CEX file with:

	> writeCex
"""

def menu:Unit = {
	println(s"\nWorks\n")
	showMe(listWorks)
	println(s"\nVersions\n")
	showMe(listVersions)
	println(s"\nTexts to be Tokenize\n")
	showMe(textsToTokenize)
	println(s"\nTexts to be Include\n")
	showMe(textsToInclude)
}

def help:Unit = { showMe(instructions) }

def writeCex(fn:String = ""):Unit = {
	/* passages to tokenize */
	val tokPassages:Vector[CtsUrn] = textsToTokenize.toVector
	val tokCorpVec:Vector[Corpus] = tokPassages.map( p => tr.corpus ~~ p)
	val tokVersionCorp:Corpus = Corpus(tokCorpVec.map(_.nodes).flatten)
	val tokCorp:Corpus = tokenizeCorpus(c = tokVersionCorp, exemplarID = defaultExemplarId)	
	val tokVersionCatEntries:Vector[CatalogEntry] = tokPassages.map(_.dropPassage).map(tr.catalog.entriesForUrn(_)).flatten.distinct
	val tokenCatEntries:Vector[CatalogEntry] = tokVersionCatEntries.map(tokenizedCatEntry(_))

	/* passages just to include as is */
	val passages:Vector[CtsUrn] = textsToInclude.toVector
	val corpVec:Vector[Corpus] = passages.map( p => tr.corpus ~~ p)
	val versionCorp:Corpus = Corpus(corpVec.map(_.nodes).flatten)
	val versionCatEntries:Vector[CatalogEntry] = passages.map(_.dropPassage).map(tr.catalog.entriesForUrn(_)).flatten.distinct

	/* combine them */
	val allCorp:Corpus = versionCorp ++ tokCorp
	val allCat:Vector[CatalogEntry] = tokenCatEntries ++ versionCatEntries

	/* make CEX and write it */
	val newCatalog:Catalog = Catalog(allCat)
	val newTr:TextRepository = TextRepository(allCorp, newCatalog)
	val cexString:String = CexWriter.writeTextRepository(newTr, true, delim1)
	val fileName:String = {
		if (fn.size == 0) {
			val base:String = defaultFileNameBase
			val now = Calendar.getInstance()
			val timeStamp:String = s"${now.get(Calendar.YEAR)}-${now.get(Calendar.MONTH)}-${now.get(Calendar.DAY_OF_MONTH)}-${now.get(Calendar.HOUR_OF_DAY)}-${now.get(Calendar.MINUTE)}"
			s"${base}-${timeStamp}.cex"
		} else {
			if (fn.endsWith(".cex")) fn else s"${fn}.cex"
		}
	}
	saveString(s = cexString, fileName = fileName)
	println(s"\nFile saved as \n\n\t${filePath}${fileName}\n")
}

println("""
---------------------
Get help with:

	> help

---------------------""")




