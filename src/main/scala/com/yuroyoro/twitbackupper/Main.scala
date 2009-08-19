package com.yuroyoro.twitbackupper

import java.text.SimpleDateFormat
import java.util.Date
import org.apache.commons.cli._

object Main {

  var arguments:Array[String] = _

  // コマンドライン引数解析
  val cliParser = new BasicParser
  val options = new Options {
    addOption(
      new Option("i", "id", true, "ID or screen name of the user for backup statuses."){
        setArgName( "id or screen name")
        setRequired( true )
      }
    )
    addOption(
      new Option("m", "max_id", true,
        "backup only statuses with an ID less than the specified ID."){
        setArgName("max id")
      }
    )
    addOption(
      new Option("s", "since_id", true,
        "backup only statuses with an ID greater than the specified ID."){
        setArgName("since id")
      }
    )
    addOption(
      new Option("t", "type", true,
        "ouput file format.xml or csv"){
        setArgName("file format")
      }
    )
    addOption(
      new Option("f", "file_name", true,
        "output filename."){
        setArgName("file name")
      }
    )
    addOption(
      new Option("interval", true , "interval for fetching one request.")
    )
    addOption(
      new Option("count", true , "count per file.max 200.default 200.")
    )
  }

  lazy val cl:CommandLine = {
    try{
      cliParser.parse(options, arguments )
    }catch{
      case e:MissingOptionException =>
        println( e.getMissingOptions() + " are required.")
        null
      case e:MissingArgumentException =>
        println( e.getOption() + " is argument required.")
        null
      case e:UnrecognizedOptionException =>
        println( "unrecognized option :" + e.getOption() )
        null
      case e => throw e
    }
  }

  def option( name:String , defaultValue:String):String= cl.getOptionValue( name ) match{
    case null => defaultValue
    case v=> v
  }
  def option( name:String , defaultValue:Long):Long= cl.getOptionValue( name ) match{
    case null => defaultValue
    case v=> v.toLong
  }


  lazy val twitterId = cl.getOptionValue("i")
  lazy val max_id = option("m" , -1 )
  lazy val since_id = option("s" , -1 )
  lazy val count = option("count", 200 )
  lazy val interval = option("interval", 1000 )
  lazy val filetype = option("t", "xml")

  val f = new SimpleDateFormat( "yyyyMMdd_HHmmss")
  lazy val filename = option("f","%s_user_timeline_%s.%s".format( twitterId, f.format( new Date ), filetype ) )

  lazy val userTimelineUrl  = "http://twitter.com/statuses/user_timeline/%s.xml?count=%d".format( twitterId, count)

  lazy val outputter:Outputter  = filetype match {
    case "xml" => XmlOutputter
    case "csv" => CsvOutputter
  }

  def main( args:Array[String] ):Unit= {
    arguments = args

    // コマンドライン引数を解析
    if( cl == null ){
      val formatter = new HelpFormatter();
      formatter.printHelp("Twitter backuuper", options )
      System.exit( -1 )
    }

    println( "%s's twitter statuses backup was started.".format( twitterId ) )

    // Actor start
    UserTimelineActor.start
    UserTimelineActor ! UserTimeline( max_id )
  }

}
