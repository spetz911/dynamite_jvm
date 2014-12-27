/**
 * Created by oleg on 12/27/14.
 *
 */


import java.io.FileOutputStream

object Main extends App {

   println("Hello, World!")

   val data = HelloDump.dump

   val fos = new FileOutputStream("/var/tmp/Hello.class")
   fos.write(data, 0, data.length)
   fos.flush()
   fos.close()

 }
