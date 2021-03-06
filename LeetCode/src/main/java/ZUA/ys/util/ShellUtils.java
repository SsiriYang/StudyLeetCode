package ZUA.ys.util;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Description
 *
 * @author YS
 * @date 2020/12/14 11:50
 */
public class ShellUtils {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ShellUtils.class);




    public static Boolean runShell(String command) {
        InputStreamReader stdISR = null;
        InputStreamReader errISR = null;
        Process process = null;
        boolean success = true;
        try {
            process = Runtime.getRuntime().exec(command);

            CommandStreamGobbler errorGobbler = new CommandStreamGobbler(process.getErrorStream(), command, "ERR");
            CommandStreamGobbler outputGobbler = new CommandStreamGobbler(process.getInputStream(), command, "STD");

            errorGobbler.start();
            // 必须先等待错误输出ready再建立标准输出
            while (!errorGobbler.isReady()) {
                Thread.sleep(10);
            }
            outputGobbler.start();
            while (!outputGobbler.isReady()) {
                Thread.sleep(10);
            }
            int exitValue = process.waitFor();
            success = errorGobbler.isSuccess() && outputGobbler.isSuccess();
        } catch (IOException | InterruptedException e) {
            success = false;
            logger.error("执行shell命令报错"+command,e);
        } finally {
            try {
                if (stdISR != null) {
                    stdISR.close();
                }
                if (errISR != null) {
                    errISR.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                logger.error("正式执行命令：" + command + "有IO异常：" + e);
            }
        }
        return success;
    }


    public static ExeRes runShell(String command, String file,Boolean endFalg) {
        InputStreamReader stdISR = null;
        InputStreamReader errISR = null;
        Process process = null;
        boolean success = true;
        ExeRes exeRes = new ExeRes();
        try {
            String[] command1 = {"/bin/sh","-c",command};
            process = Runtime.getRuntime().exec(command1);

            CommandStreamGobbler errorGobbler = new CommandStreamGobbler(process.getErrorStream(), command, "ERR", file,endFalg);
            CommandStreamGobbler outputGobbler = new CommandStreamGobbler(process.getInputStream(), command, "STD", file,endFalg);

            errorGobbler.start();
            // 必须先等待错误输出ready再建立标准输出
            while (!errorGobbler.isReady()) {
                Thread.sleep(10);
            }
            outputGobbler.start();
            while (!outputGobbler.isReady()) {
                Thread.sleep(10);
            }

            int exitValue = process.waitFor();
            exeRes.content = errorGobbler.getInfoRes().toString();
            success = errorGobbler.isSuccess() && outputGobbler.isSuccess();

        } catch (IOException | InterruptedException e) {
            success = false;
            logger.error("执行shell脚本报错"+file,e);
        } finally {
            try {
                if (stdISR != null) {
                    stdISR.close();
                }
                if (errISR != null) {
                    errISR.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                logger.error("正式执行命令：" + command + "有IO异常：" + e);
            }
        }
        exeRes.setRes(success);
        return exeRes;
    }

    public static void main(String[] args) {
        //mvn clean compile -Dmaven.test.skip=true --settings /Users/admin/developSoftware/apache-maven-3.5.4/conf/settings.xml
        //mvn clean package -Dmaven.test.skip=true --settings /Users/admin/developSoftware/apache-maven-3.5.4/conf/settings.xml
        //if [ -f  anydmp-server-*.tar.gz ]; \n then \n mv  anydmp-server-*.tar.gz   /Users/admin/developSoftware/anydmp-develop-226.tar.gz \n else \n  echo *** anydmp-server-*.tar.gz 文件不存在 \n fi
       // "git clone http://shuai.yang:yang1103@10.0.11.44:1000/anydmp/anydmp.git -b develop /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop";
       //Boolean res = ShellUtils.runShell("java -version");
        //ExeRes res1 = ShellUtils.runShell("java -version","C:\\Users\\41765\\Desktop\\AnyDmp\\123.log",Boolean.FALSE);
       // ExeRes res2 = ShellUtils.runShell("mvn clean compile -Dmaven.test.skip=true -f /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop/pom.xml --settings /Users/admin/developSoftware/apache-maven-3.5.4/conf/settings.xml","/Users/admin/Desktop/JrxWorkSpace/construct/16.log",Boolean.TRUE);
       // ExeRes res3 = ShellUtils.runShell("mvn clean package -Dmaven.test.skip=true -f /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop/pom.xml --settings /Users/admin/developSoftware/apache-maven-3.5.4/conf/settings.xml","/Users/admin/Desktop/JrxWorkSpace/construct/16.log",Boolean.TRUE);
//        ExeRes res4 = ShellUtils.runShell("cd /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop/anydmp-server/target/\n" +
//                "if [ -f anydmp-server-*.tar.gz ]\n" +
//                "then\n" +
//                "mv  anydmp-server-*.tar.gz   /Users/admin/Desktop/anydmp-develop-226.tar.gz\n" +
//                "else\n" +
//                "echo *** anydmp-server-*.tar.gz 文件不存在\n" +
//                "fi ","/Users/admin/Desktop/JrxWorkSpace/construct/16.log",Boolean.TRUE);
        ExeRes res4 = ShellUtils.runShell("if [ -f  /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop/anydmp-server/target/anydmp-server-*.tar.gz ];\n" +
                "  then\n" +
                "  mv  /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop/anydmp-server/target/anydmp-server-*.tar.gz   /Users/admin/Desktop/JrxWorkSpace/upload-file/anydmp-develop-18.tar.gz\n" +
                "  else\n" +
                "  echo *** anydmp-server-*.tar.gz 文件不存在\n" +
                "fi","/Users/admin/Desktop/JrxWorkSpace/construct/16.log",Boolean.TRUE);
        //ExeRes res4 = ShellUtils.runShell("git -C  /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop pull ","/Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop/buildLog/18.log",Boolean.TRUE);
        //ExeRes res2 = ShellUtils.runShell("git clone http://shuai.yang:yang1103@10.0.11.44:1000/anydmp/anydmp.git -b develop /Users/admin/Desktop/JrxWorkSpace/construct/anydmp/anydmp-develop","/Users/admin/Desktop/JrxWorkSpace/construct/16.log",Boolean.TRUE);
       //System.out.println(res2.getRes()+res2.getContent());
//       System.out.println(res3.getRes()+res3.getContent());
       System.out.println(res4.getRes()+res4.getContent());
    }
}
