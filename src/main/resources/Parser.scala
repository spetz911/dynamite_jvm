import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset

import org.parboiled2._



import scala.util.{Failure, Success}
import scalaparser.{CharacterClasses, WhitespaceStringsAndChars}

trait L0_BasicsX { this: Parser =>
  import CharacterClasses._

  def HexNum = rule { "0x" ~ HEXDIGIT.+ }

  def DecNum = rule { DIGIT.+ }

  def Newline = rule { '\r'.? ~ '\n' }

  def OpChar = rule { OPCHAR | test(isMathOrOtherSymbol(cursorChar)) ~ ANY }

  def AlphaNum = rule { ALPHANUM |GeneralAlphaNum }
  def AlphaNum$ = rule { ALPHANUM$ | GeneralAlphaNum }
  def AlphaNum$_ = rule { ALPHANUM$_ | GeneralAlphaNum }

  def GeneralLower = rule( `LOWER$_` | test(cursorChar.isLower) ~ ANY )

  def Comment: Rule0 = rule( BlockComment | "//" ~ (!Newline ~ ANY).* )

  /**
   * Whitespace, including newlines. This is the default for most things.
   */
  def WL = rule( (WSCHAR | Comment | Newline).* )

  /**
   * Whitespace, excluding newlines.
   * Only really useful in e.g. {} blocks, where we want to avoid
   * capturing newlines so semicolon-inference works
   */
  def WS = rule( (WSCHAR | Comment).* )

  //////////////////////////// PRIVATE ///////////////////////////////////

  private def BlockComment: Rule0 = rule( "/*" ~ (BlockComment | !"*/" ~ ANY).* ~ "*/" )

  private def GeneralAlphaNum = rule ( test(cursorChar.isLetter | cursorChar.isDigit) ~ ANY )

  private def isMathOrOtherSymbol(c: Char) =
    Character.getType(c) match {
      case Character.OTHER_SYMBOL | Character.MATH_SYMBOL => true
      case _ => false
    }
}


// our abstract syntax tree model
sealed trait SExpr
case class Leaf(value: String) extends SExpr
case class Branch(value: Seq[SExpr]) extends SExpr



trait L2_KeywordsAndOperators {
  this: Parser with L0_BasicsX =>


  def Operator = rule { OpChar.+ }

  private def IdRestWithDollar = rule { (Underscores ~ AlphaNum$.+).* ~ OpSuffix }
  private def Underscores = rule ( ch('_').* )
  private def OpSuffix = rule ( (ch('_').+ ~ OpChar.*).? )

  def PlainId = rule { AlphaNum$_  ~ IdRestWithDollar | Operator }



  def Semi = rule( WS ~ (';' | Newline.+) )
  def Semis = rule( Semi.+ )


  def `)` = rule( Semis.? ~ ')' )
  def `(` = rule( '(' ~ Semis.? )

  def Num: Rule1[SExpr] = rule { capture( DecNum ) ~>
    { x: String => println(x); Leaf(x) } }
  def Symbol: Rule1[SExpr] = rule { capture( PlainId )  ~>
    { x: String => println(x); Leaf("\'" + x + "\'") } }
  def Expr: Rule1[SExpr] = rule { '(' ~ (ExprTmp ~ WS).+ ~ ')' ~>
    { x: Seq[SExpr] => println(x); Branch(x)} }
  def ExprTmp = rule { Num | Symbol | Expr }


//  def CompilationUnitInternal = rule { ((Num | Symbol) ~ WS ).+ ~> {x => println(x);x} }

  def CompilationUnit = rule { WL ~ Expr ~ WL ~ EOI }

}


class ScalaParserX(val input: ParserInput) extends Parser with WhitespaceStringsAndChars
with L0_BasicsX
with L2_KeywordsAndOperators



object BigParser extends App {

  val fileName = "/Users/oleg/HelloWorld.scala"

  val utf8 = Charset.forName("UTF-8")

  def checkFile(path: String): Int = {
    val inputStream = new FileInputStream(path)
    val utf8Bytes = Array.ofDim[Byte](inputStream.available)
    inputStream.read(utf8Bytes)
    inputStream.close()
    val charBuffer = utf8.decode(ByteBuffer.wrap(utf8Bytes))

//    val parser = new ScalaParserX(ParserInput(charBuffer.array(), charBuffer.remaining()))

    val parser = new ScalaParserX(" (fdasf           (+ 12 1221)) ")

    parser.CompilationUnit.run() match {
      case Success(exprAst)       => println("Result: " + exprAst.toString)
      case Failure(e: ParseError) => println("Expression is not valid: " + parser.formatError(e))
      case Failure(e)             => println("Unexpected error during parsing run: " + e)
    }


    def fail(msg: String) = throw new Exception(msg)

    parser.CompilationUnit.run().failed foreach {
      case error: ParseError => fail(s"Error in file `$path`:\n" + parser.formatError(error))
      case error => fail(s"Exception in file `$path`:\n$error")
    }

    println("val fileChars = " + parser.input.length.toString)

    parser.input.length
  }

  val exampleName = "scalaCode"
  val startTime = System.nanoTime()
  val fileChars = checkFile(fileName)
  val totalChars = fileChars / 1000
  val millis = (System.nanoTime() - startTime)/1000000
  println(s"$exampleName:\n  ${totalChars}K chars in $millis ms (${totalChars*1000/millis}K chars/sec})")


}
