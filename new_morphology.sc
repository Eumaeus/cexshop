
import scala.io.Source
import java.io._
import java.util.Calendar
import scala.collection.mutable.LinkedHashMap
import edu.holycross.shot.scm._
import edu.holycross.shot.cite._
import edu.holycross.shot.ohco2._
import edu.holycross.shot.greek._
import scala.io.StdIn.readLine
import scala.collection.mutable._
import edu.furman.classics.fumorph._

:load newMorphConfig.sc

val filePath:String = "" 
val splitters:String = """[\[\])(·⸁.,·;;   "?·!–—⸂⸃]"""

def loadLibrary(fp:String):CiteLibrary = {
	val library = CiteLibrary(Source.fromFile(fp).getLines.mkString("\n"),"#",",")
	library
}

def loadFile(fp:String):Vector[String] = {
	Source.fromFile(fp).getLines.toVector
}


def saveString(s:String, fileName:String = "cex/test.cex"):Unit = {
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

/* Variables Below */

val greekLexIndex = loadFile("lexica/new_greek_index.txt").filter(_.split("#").size == 3)
//val gmlib = loadLibrary(greekMorphLibFile)
//val gtlib = loadLibrary("cex/candaules.cex)
//lazy val gmorph:FuMorph = FuMorph(Some(gmlib), gtlib, Greek, greekLexIndex)

val latinLexIndex = loadFile("lexica/new_latin_index.txt").filter(_.split("#").size == 3)
//val lmlib = loadLibrary(greekMorphLibFile)
//val ltlib = loadLibrary("cex/candaules.cex")
//lazy val lmorph:FuMorph = FuMorph(Some(lmlib), ltlib, Latin, latinLexIndex)

//Load and update
var morphLib:Option[FuMorph] = None
var morphLang:MorphLanguage = lang

def newMorph(libLang:MorphLanguage, textLibFile:String = textLibrary):Unit = {
	val textLib:CiteLibrary = loadLibrary(textLibFile)
	morphLang match {
		case Greek => {
			val gmorph:FuMorph = FuMorph(None, textLib, Greek)	
			morphLib = Some(gmorph)
		}
		case _ => {
			val lmorph:FuMorph = FuMorph(None, textLib, Latin)
			morphLib = Some(lmorph)
		}
	}
}

def newMorphology:Unit = {
	newMorph(lang, textLibrary)
	saveString(morphLib.get.updateCex, outputFile)
}

def help:Unit = {

println("""---------------------

Start a new morphology:

		[ edit file newMorphConfig.sc ]

		scala> newMorphology

---------------------""")
}

help