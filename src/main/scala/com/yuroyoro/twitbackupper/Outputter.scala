package com.yuroyoro.twitbackupper

import Main.filename

import scala.xml._
import scala.collection.mutable.ArrayBuffer
import java.io.{Writer, OutputStreamWriter, FileOutputStream }

trait Outputter {
  lazy val os:Writer = new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8")

  def add( xml:Node ):Unit
  def close = {
    os.flush
    os.close
  }
}

object XmlOutputter extends Outputter {

  os.write("<statuses type='array'>\n")

  def add( xml:Node ) = os.write( (xml \\ "status").toString + "\n")

  override def close = {
    os.write("</statuses>")
    os.flush
    os.close
  }
}

object CsvOutputter extends Outputter{

  val header = List("created_at", "id", "text", "source", "truncated", "in_reply_to_status_id", "in_reply_to_user_id", "favorited", "in_reply_to_screen_name")

  os.write( header.mkString("\"","\",\"","\"\n") )

  def add( xml:Node ) = {
    for (s <- xml \ "status" ;
      created_at              = s \ "created_at" text ;
      id                      = s \ "id" text ;
      text                    = s \ "text" text ;
      source                  = s \ "source" text ;
      truncated               = s \ "truncated" text ;
      in_reply_to_status_id   = s \ "in_reply_to_status_id" text ;
      in_reply_to_user_id     = s \ "in_reply_to_user_id" text ;
      favorited               = s \ "favorited" text ;
      in_reply_to_screen_name = s \ "in_reply_to_screen_name" text ){

      val l =  List(created_at, id, text, source, truncated, in_reply_to_status_id, in_reply_to_user_id, favorited, in_reply_to_screen_name).map( s => s.replace("\"", "\"\""))
      os.write( l.mkString("\"","\",\"","\"\n") )
    }
  }
}
