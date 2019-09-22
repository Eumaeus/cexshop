
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
val splitters:String = """[\[\])(·⸁.,:·;;   "?·!–—⸂⸃]"""

def splitWithSplitter(text:String, splitters:String = """[\[\]··⸁.; "?!–—⸂⸃]"""):Vector[String] = {
	val regexWithSplitter = s"(?<=${splitters})"
	text.split(regexWithSplitter).toVector.filter(_.size > 0)
}


def loadLibrary(fp:String = "cex/library.cex"):CiteLibrary = {
	val library = CiteLibrary(Source.fromFile(fp).getLines.mkString("\n"),"#",",")
	library
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
		case _:Vector[Any] => println(s"""----\n${v.asInstanceOf[Vector[Any]].mkString("\n")}\n----""")
		case _:Iterable[Any] => println(s"""----\n${v.asInstanceOf[Iterable[Any]].mkString("\n")}\n----""")
		case _ => println(s"-----\n${v}\n----")
	}
}


/* Task-specific stuff below! */

val libraryFile:String = "cex/library.cex"

// For CEX Generation
lazy val lib:CiteLibrary = loadLibrary(libraryFile)

lazy val tr:TextRepository = lib.textRepository.get

lazy val corp:Corpus = tr.corpus


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

