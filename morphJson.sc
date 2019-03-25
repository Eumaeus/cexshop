
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
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import io.circe.parser.decode
import cats.syntax.either._
import io.circe.optics.JsonPath._


val test1:String = """{"RDF": {"Annotation": {"about": "urn:TuftsMorphologyService:oi(:morpheusgrc", "creator": {"Agent": {"about": "org.perseus:tools:morpheus.v1"}}, "created": {"$": "2019-03-20T14:53:20.812936"}, "hasTarget": {"Description": {"about": "urn:word:oi("}}, "title": {}, "hasBody": [{"resource": "urn:uuid:idm140550556017072"}, {"resource": "urn:uuid:idm140550555391472"}], "Body": [{"about": "urn:uuid:idm140550556017072", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "ἕ"}, "pofs": {"order": 5, "$": "pronoun"}}, "infl": [{"term": {"lang": "grc", "stem": {"$": "οἱ"}}, "pofs": {"order": 5, "$": "pronoun"}, "case": {"order": 5, "$": "dative"}, "gend": {"$": "masculine"}, "num": {"$": "singular"}, "dial": {"$": "epic Ionic"}, "stemtype": {"$": "pron3"}, "morph": {"$": "enclitic indeclform"}}, {"term": {"lang": "grc", "stem": {"$": "οἱ"}}, "pofs": {"order": 5, "$": "pronoun"}, "case": {"order": 5, "$": "dative"}, "gend": {"$": "feminine"}, "num": {"$": "singular"}, "dial": {"$": "epic Ionic"}, "stemtype": {"$": "pron3"}, "morph": {"$": "enclitic indeclform"}}]}}}, {"about": "urn:uuid:idm140550555391472", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "ὁ"}, "pofs": {"order": 0, "$": "article"}}, "infl": [{"term": {"lang": "grc", "stem": {"$": "οἱ"}}, "pofs": {"order": 0, "$": "article"}, "case": {"order": 7, "$": "nominative"}, "gend": {"$": "masculine"}, "num": {"$": "plural"}, "stemtype": {"$": "article"}, "morph": {"$": "proclitic indeclform"}}, {"term": {"lang": "grc", "stem": {"$": "οἱ"}}, "pofs": {"order": 0, "$": "article"}, "case": {"order": 1, "$": "vocative"}, "gend": {"$": "masculine"}, "num": {"$": "plural"}, "stemtype": {"$": "article"}, "morph": {"$": "proclitic indeclform"}}]}}}]}}}"""

val test2:String = """{"RDF": {"Annotation": {"about": "urn:TuftsMorphologyService:tw=n:morpheusgrc", "creator": {"Agent": {"about": "org.perseus:tools:morpheus.v1"}}, "created": {"$": "2019-03-20T14:53:21.006266"}, "hasTarget": {"Description": {"about": "urn:word:tw=n"}}, "title": {}, "hasBody": {"resource": "urn:uuid:idm140550555259904"}, "Body": {"about": "urn:uuid:idm140550555259904", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "ὁ"}, "pofs": {"order": 0, "$": "article"}}, "infl": [{"term": {"lang": "grc", "stem": {"$": "τῶν"}}, "pofs": {"order": 0, "$": "article"}, "case": {"order": 6, "$": "genitive"}, "gend": {"$": "feminine"}, "num": {"$": "plural"}, "stemtype": {"$": "article"}, "morph": {"$": "indeclform"}}, {"term": {"lang": "grc", "stem": {"$": "τῶν"}}, "pofs": {"order": 0, "$": "article"}, "case": {"order": 6, "$": "genitive"}, "gend": {"$": "masculine"}, "num": {"$": "plural"}, "stemtype": {"$": "article"}, "morph": {"$": "indeclform"}}, {"term": {"lang": "grc", "stem": {"$": "τῶν"}}, "pofs": {"order": 0, "$": "article"}, "case": {"order": 6, "$": "genitive"}, "gend": {"$": "neuter"}, "num": {"$": "plural"}, "stemtype": {"$": "article"}, "morph": {"$": "indeclform"}}]}}}}}}"""

val test3:String = """{"RDF": {"Annotation": {"about": "urn:TuftsMorphologyService:*dasku/lou:morpheusgrc", "creator": {"Agent": {"about": "org.perseus:tools:morpheus.v1"}}, "created": {"$": "2019-03-20T14:53:21.940477"}, "hasTarget": {"Description": {"about": "urn:word:*dasku/lou"}}, "title": {}, "hasBody": {"resource": "urn:uuid:idm140550555993248"}, "Body": {"about": "urn:uuid:idm140550555993248", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "Δάσκυλος"}, "pofs": {"order": 3, "$": "noun"}, "decl": {"$": "2nd"}, "gend": {"$": "masculine"}}, "infl": {"term": {"lang": "grc", "stem": {"$": "Δασκυλ"}, "suff": {"$": "ου"}}, "pofs": {"order": 3, "$": "noun"}, "decl": {"$": "2nd"}, "case": {"order": 6, "$": "genitive"}, "gend": {"$": "masculine"}, "num": {"$": "singular"}, "stemtype": {"$": "os_ou"}}}}}}}}"""

val test4:String = """{"RDF": {"Annotation": {"about": "urn:TuftsMorphologyService:u(perepaine/wn:morpheusgrc", "creator": {"Agent": {"about": "org.perseus:tools:morpheus.v1"}}, "created": {"$": "2019-03-20T14:53:27.362013"}, "hasTarget": {"Description": {"about": "urn:word:u(perepaine/wn"}}, "title": {}, "hasBody": {"resource": "urn:uuid:idm140550553759312"}, "Body": {"about": "urn:uuid:idm140550553759312", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "ὑπερεπαινέω"}, "pofs": {"order": 1, "$": "verb"}}, "infl": [{"term": {"lang": "grc", "stem": {"$": "ὑπερεπαιν"}, "suff": {"$": "έων"}}, "pofs": {"order": 0, "$": "verb participle"}, "case": {"order": 7, "$": "nominative"}, "gend": {"$": "masculine"}, "mood": {"$": "participle"}, "num": {"$": "singular"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "epic Doric Ionic Aeolic"}, "stemtype": {"$": "ew_pr"}, "derivtype": {"$": "ew_denom"}}, {"term": {"lang": "grc", "stem": {"$": "ὑπέρ,ἐπί:αἰν"}, "suff": {"$": "έων"}}, "pofs": {"order": 0, "$": "verb participle"}, "case": {"order": 7, "$": "nominative"}, "gend": {"$": "masculine"}, "mood": {"$": "participle"}, "num": {"$": "singular"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "epic Doric Ionic Aeolic"}, "stemtype": {"$": "ew_pr"}, "derivtype": {"$": "e_stem"}}]}}}}}}"""

val test5:String = """{"RDF": {"Annotation": {"about": "urn:TuftsMorphologyService:xrh=n:morpheusgrc", "creator": {"Agent": {"about": "org.perseus:tools:morpheus.v1"}}, "created": {"$": "2019-03-20T14:53:29.222974"}, "hasTarget": {"Description": {"about": "urn:word:xrh=n"}}, "title": {}, "hasBody": [{"resource": "urn:uuid:idm140550556702880"}, {"resource": "urn:uuid:idm140550556519392"}, {"resource": "urn:uuid:idm140550554900880"}, {"resource": "urn:uuid:idm140550554832304"}, {"resource": "urn:uuid:idm140550554144080"} ], "Body": [{"about": "urn:uuid:idm140550556702880", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χράω1"}, "pofs": {"order": 1, "$": "verb"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "Doric Ionic"}, "stemtype": {"$": "aw_pr"}, "derivtype": {"$": "a_stem"}, "morph": {"$": "contr"} } }} }, {"about": "urn:uuid:idm140550556519392", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χράω2"}, "pofs": {"order": 1, "$": "verb"} }, "infl": [{"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "Attic epic Doric"}, "stemtype": {"$": "ajw_pr"}, "derivtype": {"$": "a_stem"}, "morph": {"$": "contr"} }, {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "epic Doric Ionic"}, "stemtype": {"$": "ew_pr"}, "derivtype": {"$": "a_stem"}, "morph": {"$": "contr"} } ] }} }, {"about": "urn:uuid:idm140550554900880", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χραύω"}, "pofs": {"order": 1, "$": "verb"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "Doric Ionic"}, "stemtype": {"$": "aw_pr"}, "derivtype": {"$": "av_stem"}, "morph": {"$": "contr"} } }} }, {"about": "urn:uuid:idm140550554832304", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χρή"}, "pofs": {"order": 1, "$": "verb"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "indicative"}, "num": {"$": "singular"}, "pers": {"$": "3rd"}, "tense": {"$": "imperfect"}, "voice": {"$": "active"}, "stemtype": {"$": "ath_primary"} } }} }, {"about": "urn:uuid:idm140550554144080", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χρῆ"}, "pofs": {"order": 3, "$": "noun"}, "decl": {"$": "1st"}, "gend": {"$": "feminine"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 3, "$": "noun"}, "decl": {"$": "1st"}, "case": {"order": 4, "$": "accusative"}, "gend": {"$": "feminine"}, "num": {"$": "singular"}, "dial": {"$": "epic Ionic"}, "stemtype": {"$": "eh_ehs"}, "morph": {"$": "contr"} } }} } ] }}}"""

val test6:String = """{"Body": [{"about": "urn:uuid:idm140550556702880", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χράω1"}, "pofs": {"order": 1, "$": "verb"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "Doric Ionic"}, "stemtype": {"$": "aw_pr"}, "derivtype": {"$": "a_stem"}, "morph": {"$": "contr"} } }} }, {"about": "urn:uuid:idm140550556519392", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χράω2"}, "pofs": {"order": 1, "$": "verb"} }, "infl": [{"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "Attic epic Doric"}, "stemtype": {"$": "ajw_pr"}, "derivtype": {"$": "a_stem"}, "morph": {"$": "contr"} }, {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "epic Doric Ionic"}, "stemtype": {"$": "ew_pr"}, "derivtype": {"$": "a_stem"}, "morph": {"$": "contr"} } ] }} }, {"about": "urn:uuid:idm140550554900880", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χραύω"}, "pofs": {"order": 1, "$": "verb"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "infinitive"}, "tense": {"$": "present"}, "voice": {"$": "active"}, "dial": {"$": "Doric Ionic"}, "stemtype": {"$": "aw_pr"}, "derivtype": {"$": "av_stem"}, "morph": {"$": "contr"} } }} }, {"about": "urn:uuid:idm140550554832304", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χρή"}, "pofs": {"order": 1, "$": "verb"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρῆν"} }, "pofs": {"order": 1, "$": "verb"}, "mood": {"$": "indicative"}, "num": {"$": "singular"}, "pers": {"$": "3rd"}, "tense": {"$": "imperfect"}, "voice": {"$": "active"}, "stemtype": {"$": "ath_primary"} } }} }, {"about": "urn:uuid:idm140550554144080", "type": {"resource": "cnt:ContentAsXML"}, "rest": {"entry": {"uri": null, "dict": {"hdwd": {"lang": "grc", "$": "χρῆ"}, "pofs": {"order": 3, "$": "noun"}, "decl": {"$": "1st"}, "gend": {"$": "feminine"} }, "infl": {"term": {"lang": "grc", "stem": {"$": "χρ"}, "suff": {"$": "ῆν"} }, "pofs": {"order": 3, "$": "noun"}, "decl": {"$": "1st"}, "case": {"order": 4, "$": "accusative"}, "gend": {"$": "feminine"}, "num": {"$": "singular"}, "dial": {"$": "epic Ionic"}, "stemtype": {"$": "eh_ehs"}, "morph": {"$": "contr"} } }} } ]}"""

/**
    * Returns a List[Json] consisting of the contents of the "infl" element.
    **/
    def processInflectionJson(str:String):List[Json] = {
      val json:Json = parse(str).getOrElse(Json.Null)
      println(json)
      val cursor:HCursor = json.hcursor
      /* the `infl` thing in the JSON is either an object or a list */
      val inflListOpt:Option[List[Json]] = cursor
        .downField("RDF")
        .downField("Annotation")
        .downField("Body")
        .downField("rest")
        .downField("entry")
        .downField("infl")
        .as[List[Json]].toOption
      println(inflListOpt)

      val inflObjOpt:Option[Json] = cursor.downField("rest").downField("entry").downField("infl").as[Json].toOption
      println(inflObjOpt)

      /* If neither, throw an error */
      if ((inflListOpt == None) & (inflObjOpt == None)) {
        println(s"\n\nERROR! No Inflections Found!\n\n")
      }

      /* Make a list, one way or another */
      val inflList:List[Json] = {
        inflListOpt match {
          case Some(il) => {
            il
          }
          case None => {
            List(inflObjOpt.getOrElse(Json.Null))
          }
        }
      }
      inflList
    }

def getSurfaceForm(cursor:HCursor):Decoder.Result[String] = {
  	cursor
  		.downField("RDF")
  		.downField("Annotation")
  		.downField("hasTarget")
  		.downField("Description")
  		.downField("about")
  		.as[String]
}

def getBody(cursor:HCursor):Json = {
  val jOpt:Option[Json] = cursor
      .downField("RDF")
      .downField("Annotation")
      .downField("Body").as[Json].toOption
  jOpt match {
    case Some(j) => j
    case None => Json.Null
  }
}

def getCursor(str:String):HCursor = {
  val doc:Json = parse(str).getOrElse(Json.Null)
  if (doc == Json.Null) throw new Exception(s"Invalid JSON")
  doc.hcursor
}

val c:HCursor = getCursor(test1)

println(s"""try: cursor.downField("Body").as[List[Json]].toOption.get""")
