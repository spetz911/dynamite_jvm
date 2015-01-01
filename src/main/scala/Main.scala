/**
 * Created by oleg on 12/27/14.
 *
 */


import java.io.FileOutputStream

object Main extends App {

   println("Hello, World!")

   val md = new MainDump

   val data = md.testIt

   val fos = new FileOutputStream("/var/tmp/Main.class")
   fos.write(data, 0, data.length)
   fos.flush()
   fos.close()

 }
