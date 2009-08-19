package com.yuroyoro.twitbackupper

import scala.actors._
import scala.actors.Actor._
import scala.xml._
import java.lang.Thread
import java.net.{URL, HttpURLConnection,URLEncoder}

/**
 * UserTimelineを取得するActor
 */
object UserTimelineActor extends Actor{
  import Main._

  def act = {
    var cnt:Long = 0
    loop{
      react{
        case UserTimeline( maxId ) =>
          getUserTimeline( maxId ) match {
            case (xml:Node , nextMaxId:Long ) =>
              // statusが取れたらFileに出力
              outputter.add( xml )

              println("%d - %d statuses fetched.".format(cnt, cnt + 200 ))
              cnt += 200

              Thread.sleep(interval)
              // 次のmax_idを指定して自身にメッセージ投げる
              this ! UserTimeline( nextMaxId  - 1)

            case _ =>
              outputter.close
              println( "%s's twitter statuses backup was completed.file=%s".format( twitterId , filename) )
              exit
        }
      }
    }
  }

  def getUserTimeline( maxId:Long )= {
    // UserTimelineを取得する
    val url = userTimelineUrl +
      { if( maxId > 0 ) "&max_id=" + maxId else ""} +
      { if( since_id > 0 ) "&since_id=" + since_id else ""}

    println( url )
    val urlConn = new URL(url).openConnection.asInstanceOf[HttpURLConnection]

    urlConn.connect
    urlConn.getResponseCode
    // XML取得
    val xml = XML.load(urlConn.getInputStream)
    xml \\ "status" size match {
      // statusが取れなくなったら終了
      case 0  => None
      // XMLと、取得した中で最小のstatus idをTupleにして返す
      case _ => ( xml ,  (xml \\ "status" \ "id"  last ).text.toLong )
    }
  }
}

// Actorに投げるメッセージ
case class UserTimeline( maxId:Long )
