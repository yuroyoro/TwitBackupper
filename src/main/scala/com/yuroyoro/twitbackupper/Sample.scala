package com.yuroyoro.twitbackupper

import scala.xml._
import scala.io.Source

object Sample {

  def main( args:Array[String] ):Unit= {
    val twitterId = args.first
    lazy val userTimelineUrl = "http://twitter.com/statuses/user_timeline/%s.xml?count=200".format( twitterId )

    def getUserTimeline( maxId:Long , cnt:Int):Unit = {
      // UserTimelineを取得する
      val url = userTimelineUrl + { if( maxId > 0 ) "&max_id=" + ( maxId - 1 ) else ""}
      println( url )
      val source = Source.fromURL( url )

      val xml = XML.loadString( source.getLines.mkString )// XML取得
      xml \\ "status" size match {
        // statusが取れなくなったら終了
        case 0  => None
        // XMLをファイルに書き出して再帰
        case _ =>
          XML.saveFull( "%s_user_timeline_%d.xml".format( twitterId, cnt ), xml, "UTF-8", false, null)
          getUserTimeline( (xml \\ "status" \ "id" last).text.toLong , cnt + 1)
      }
    }
    getUserTimeline( -1 , 0)
  }
}
