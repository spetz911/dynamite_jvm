import java.util.*;
import org.objectweb.asm.*;

import static java.lang.System.exit;

public class MainDump implements Opcodes {

    int currStack;
    int operandStack;
    ClassWriter cw = new ClassWriter(0);
    MethodVisitor mv;

    public byte[] testIt() {
        Tree cond = new Tree(Arrays.asList(new Tree("<"), new Tree(6), new Tree(1)));

        Tree prog = new Tree(Arrays.asList(new Tree("if"), cond, new Tree(4), new Tree(8)));

        cw.visit(52, ACC_SUPER, "Main", null, "java/lang/Object", null);

        {
            mv = cw.visitMethod(0, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "factorial", "(I)I", null, null);
            mv.visitCode();

            int valN = 0;
            currStack = 1;

            handleValue(prog);

            mv.visitInsn(IRETURN);


            mv.visitMaxs(7, 5);
            mv.visitEnd();
        }


        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitInsn(ICONST_2);
            mv.visitMethodInsn(INVOKESTATIC, "Main", "factorial", "(I)I", false);
            mv.visitVarInsn(ISTORE, 1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 5);
            mv.visitEnd();
        }



        cw.visitEnd();

        return cw.toByteArray();

    }

    public int handleIfThenElse(Tree cond, Tree thenBr, Tree elseBr) {
        String cmpFun = cond.children.get(0).str_value;
        handleValue(cond.children.get(1));
        handleValue(cond.children.get(2));

        Label l0 = new Label();

        switch (cmpFun) {
            case "<":
                mv.visitJumpInsn(IF_ICMPGE, l0);
                break;
            case "==":
                mv.visitJumpInsn(IF_ICMPNE, l0);
                break;
            default:
                exit(1);
        }
        handleValue(thenBr);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        handleValue(elseBr);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
        return 0;
    }

    public int handleCall(String fun, List<Tree> args) {

        args.forEach(this::handleValue);

        switch (fun) {
            case "+":
                mv.visitInsn(IADD);
                operandStack--;
                return operandStack;
            case "-":
                mv.visitInsn(ISUB);
                operandStack--;
                return operandStack;
            case "*":
                mv.visitInsn(IMUL);
                operandStack--;
                return operandStack;
            default:
                exit(1);
                return -1;
        }
    }

    public int handleValue(Tree expr) {
        System.out.println(expr.kind);
        if (Objects.equals(expr.kind, "ABranch") &&
                Objects.equals(expr.children.get(0).str_value, "if")) {
            handleIfThenElse(expr.children.get(1), expr.children.get(2), expr.children.get(3));
            return 0;
        } else if (Objects.equals(expr.kind, "AString")) {
            return -1;
        } else if (Objects.equals(expr.kind, "ANumber")) {
            mv.visitIntInsn(BIPUSH, expr.num_value);
            return -1;
        } else if (Objects.equals(expr.kind, "ABranch")) {
            String fun = expr.children.get(0).str_value;
            List<Tree> args = new ArrayList<>();
            for (int i=1; i < expr.children.size(); ++i) {
                args.add(expr.children.get(i));
            }
            return handleCall(fun, args);
        } else {
            exit(1);
            return -1;
        }
    }


    public void handleFunction(ClassWriter cw, MethodVisitor mv) {
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "factorial", "(I)I", null, null);
        mv.visitCode();

        int valN = 0;
        currStack = 1;

        mv.visitInsn(ICONST_0);
        int valIfRes = currStack++;
        mv.visitVarInsn(ISTORE, valIfRes);

        mv.visitVarInsn(ILOAD, valN);

        mv.visitInsn(ICONST_1);
        Label l0 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l0);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, valIfRes);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, valN);

        mv.visitVarInsn(ILOAD, valN);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(ISUB);

        mv.visitMethodInsn(INVOKESTATIC, "Hell", "factorial", "(I)I", false);
        mv.visitInsn(IMUL);
        mv.visitVarInsn(ISTORE, valIfRes);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }



    public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
        MethodVisitor mv;
AnnotationVisitor av0;

        cw.visit(52, ACC_SUPER, "Hell", null, "java/lang/Object", null);

{
mv = cw.visitMethod(0, "<init>", "()V", null, null);
mv.visitCode();
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(1, 1);
mv.visitEnd();
}


        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitInsn(ICONST_2);
            mv.visitMethodInsn(INVOKESTATIC, "Main", "factorial", "(I)I", false);
            mv.visitVarInsn(ISTORE, 1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 5);
            mv.visitEnd();
        }



cw.visitEnd();

return cw.toByteArray();
}
}
