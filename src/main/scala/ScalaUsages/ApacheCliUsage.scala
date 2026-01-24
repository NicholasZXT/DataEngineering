package ScalaUsages

import org.apache.commons.cli.{
  Option, Options, CommandLineParser, DefaultParser, CommandLine,
  HelpFormatter, MissingArgumentException, ParseException
}

/**
 * 展示 Scala 中 Apache Common CLI 的使用
 */
object ApacheCliUsage extends App {

  val options: Options = new Options()
  val helpOption: Option = Option.builder("h")
    .longOpt("help")
    .hasArg(false)
    .required(false)
    .desc("print help message")
    .build()
  val versionOption: Option = Option.builder("v")
    .longOpt("version")
    .hasArg(false)
    .required(false)
    .desc("print the version information and exit")
    .build()
  val fileOption: Option = Option.builder("f")
    .longOpt("file")
    .hasArg(true)
    .required(true)
    .argName("file")
    .`type`(classOf[String])
    .desc("the file to process")
    .build()
  options
    .addOption(helpOption)
    .addOption(versionOption)
    .addOption(fileOption)

  val cmdArgs: Array[String] = Array("-h", "-v", "-f", "some-file.txt")
  val parser: CommandLineParser = new DefaultParser()
  val cmd: CommandLine = try {
    parser.parse(options, cmdArgs)
  } catch {
    case e: MissingArgumentException =>
      println(e.getMessage)
      null
    case e: ParseException =>
      println(e.getMessage)
      null
  }

  //打印帮助信息
  println("-------------- help info ---------------")
  val formatter: HelpFormatter = new HelpFormatter()
  formatter.printHelp("ApacheCliUsage", options)

  // 打印解析参数
  println("-------------- args parsed ---------------")
  println("helpOption: " + cmd.hasOption("h"))
  println("versionOption: " + cmd.hasOption("v"))
  println("fileOption: " + cmd.hasOption("f"))
  println("fileOption.value: " + cmd.getOptionValue("file"))
}
