import org.objectweb.asm.Opcodes._
import org.objectweb.asm._

object HelloDump {
  @throws(classOf[Exception])
  def dump: Array[Byte] = {
    val cw: ClassWriter = new ClassWriter(0)
    val fv: FieldVisitor = null
    var mv: MethodVisitor = null
    val av0: AnnotationVisitor = null
    cw.visit(52, ACC_SUPER, "Hello", null, "java/lang/Object", null)
    mv = cw.visitMethod(0, "<init>", "()V", null, null)
    mv.visitCode()
    mv.visitVarInsn(ALOAD, 0)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
    mv.visitInsn(RETURN)
    mv.visitMaxs(1, 1)
    mv.visitEnd()

    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null)
    mv.visitCode()
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
    mv.visitLdcInsn("Hello World!")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
    mv.visitInsn(RETURN)
    mv.visitMaxs(2, 1)
    mv.visitEnd()
    cw.visitEnd()
    cw.toByteArray
  }
}
