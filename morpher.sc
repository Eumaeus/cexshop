
import scala.io.Source
import java.io._
import java.util.Calendar
import scala.collection.mutable.LinkedHashMap
import edu.holycross.shot.scm._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.greek._
import edu.holycross.shot.seqcomp._
import edu.furman.classics.citewriter._
import scala.io.StdIn.readLine
import scala.collection.mutable._


val filePath:String = "cex/" 
val splitters:String = """[\[\])(·⸁.,·;;   "?·!–—⸂⸃]"""

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

def morphServiceUrl(token:String, lang:String):String = s"http://services.perseids.org/bsp/morphologyservice/analysis/word?lang=${lang}&engine=morpheus${lang}&word=${token}"

def getMorph(s:String, lang:String = "grc"):String = {
	if ( (lang == "grc") | (lang == "lat")) {	
		val raw:String = scala.io.Source.fromURL(morphServiceUrl(s, lang)).mkString
		raw
	} else { "" }
}

def analyzeText(c:Corpus, lang:String):Vector[(CtsUrn, String, String, String)] = {
	val tups:Vector[(CtsUrn, String, String, String)] = {
		c.nodes.filter(_.text.replaceAll(splitters,"").size > 0).map( n => {
			val u:CtsUrn = n.urn
			val s:String = n.text.replaceAll(splitters,"")
			val str:String = {
				if (lang == "grc") {
					val gs:LiteraryGreekString = LiteraryGreekString(s)
					gs.ascii.replaceAll("\\\\","/")
				} else {
					s
				}
			}
			val url:String = morphServiceUrl(str,lang)
			try {
				println(s""" … (${lang}) "${s}" >> "${str}" """)
				val morph:String = getMorph(str, lang)
				(u, s, url, morph)
			} catch {
				case e:Exception => {
					println(s"""ERROR on "${s}" (${u}) """)
					(u, s, url, s"ERROR")
				}
			}
		})
	}
	println(s"Analyzed ${c.nodes.size} tokens into ${tups.size} analyses.")
	tups
}

def saveTest(c:Corpus, lang:String) = {
	val jString:String = analyzeText(c, lang).map( u => {
		s"""urn: ${u._1}\nstring: "${u._2}\n${u._3}\n\n${u._4}"""
	}).mkString("\n---------------\n")

	saveString(jString, fileName = "testJson.txt")
}

/* Task-specific stuff below! */

val libraryFile:String = "cex/candaules.cex"


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

val h = CtsUrn("urn:cts:greekLit:tlg0016.tlg001.grc.token:1.8")
//val h = CtsUrn("urn:cts:croala:kunicr.ilias.croala_ohco2:1.1-1.10")
val c = corp ~~ h

