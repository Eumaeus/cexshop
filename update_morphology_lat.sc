
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

:load morphConfig_lat.sc

val filePath:String = "" 
val splitters:String = """[\[\])(·⸁.,·:…;;   "?·!–—⸂⸃]"""

def loadLibrary(fp:String):CiteLibrary = {
	val library = CiteLibrary(Source.fromFile(fp).getLines.mkString("\n"),"#",",")
	library
}

def loadFile(fp:String):Vector[String] = {
	Source.fromFile(fp).getLines.toVector
}


def saveString(s:String, fileName:String):Unit = {
	val pw = new PrintWriter(new File(filePath + fileName))
		pw.append(s)
		pw.append("\n")
	pw.close
}

def showMe(v:Any):Unit = {
	v match {
		case _:Iterable[Any] => println(s"""----\n${v.asInstanceOf[Iterable[Any]].mkString("\n")}\n----""")
		case _:Vector[Any] => println(s"""----\n${v.asInstanceOf[Vector[Any]].mkString("\n")}\n----""")
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

// Save backup of current morphology 
def backup(lib:Option[FuMorph], fileName:String = outputFile):Unit = {
	lib match {
		case Some(l) => {
			val oldMorphology:String = l.backupCex
			saveString(oldMorphology, fileName + ".bak" )
		}
		case None => {
			println("No library loaded.")
		}
	}
}


//Save new morphology
def update(lib:Option[FuMorph], fileName:String = outputFile):Unit = {
	lib match {
		case Some(l) => {
			val newMorphology:String = l.updateCex
			saveString(newMorphology, fileName )
		}
		case None => {
			println("No library loaded.")
		}
	}
}


def loadMorph(libLang:MorphLanguage = morphLang, textLibFile:String = textLibrary) = {
	val textLib:CiteLibrary = loadLibrary(textLibFile)
	morphLang match {
		case Greek => {
			val gmlib = loadLibrary(outputFile)
			val gmorph:FuMorph = FuMorph(Some(gmlib), textLib, morphLang)	
			morphLib = Some(gmorph)
		}
		case _ => {
			val lmlib = loadLibrary(outputFile)
			val lmorph:FuMorph = FuMorph(Some(lmlib), textLib, morphLang)
			morphLib = Some(lmorph)
		}
	}
}

def updateMorphology:Unit = {
	loadMorph()
	update(morphLib)
}

def help:Unit = {

println("""---------------------
Build on an existing morphology: 

		[ edit file morphConfig_lat.sc ]

		scala> updateMorphology

---------------------""")
}

help