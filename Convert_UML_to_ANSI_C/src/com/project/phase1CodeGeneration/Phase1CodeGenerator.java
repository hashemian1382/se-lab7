package com.project.phase1CodeGeneration;

import org.javatuples.Pair;

import java.io.*;
import java.util.Vector;

public class Phase1CodeGenerator {

    boolean successFull;

    public Phase1CodeGenerator(CompleteDiagram diagram)
    {
        successFull = false;
        if(diagram.isSuccessCode())
        {
            try {
                ensureDirectoriesExist();

                writeAllClassesHeader(diagram);
                writeOverloadFile(diagram);
                writeDiagramInfo(diagram);

                for(CompleteClass completeClass:diagram.getClasses())
                {
                    writeClassFiles(completeClass, diagram.allClassNames());
                }
                successFull = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ensureDirectoriesExist() throws IOException {
        new File("phase1").mkdir();
        new File("headers").mkdir();
        new File("diagram_info").mkdir();
    }

    private void writeAllClassesHeader(CompleteDiagram diagram) throws IOException {
        OutputStream headersOutputStream = new FileOutputStream("headers/AllClasses.h");
        headersOutputStream.write(
                CodeGenerationUtils.generateAllClassesHeaderFile(diagram.allClassNames(),"AllClasses.h").getBytes());
        headersOutputStream.flush();
        headersOutputStream.close();
    }

    private void writeOverloadFile(CompleteDiagram diagram) throws IOException {
        OutputStream overloadOutputStream = new FileOutputStream("headers/overload.h");
        overloadOutputStream.write(
                CodeGenerationUtils.generateOverloadFile(diagram.allClassNames(), "overload.h").getBytes());
        overloadOutputStream.flush();
        overloadOutputStream.close();
    }

    private void writeDiagramInfo(CompleteDiagram diagram) throws IOException {
        OutputStream allInfoOutputStream = new FileOutputStream("diagram_info/AllClasses.info");
        allInfoOutputStream.write(diagram.generateClassNamesSeparatedByNewline().getBytes());
        allInfoOutputStream.flush();
        allInfoOutputStream.close();
    }

    private void writeClassFiles(CompleteClass completeClass, Vector<String> allClassNames) throws IOException {
        OutputStream CPPFileOutputStream =
                new FileOutputStream("phase1/" + completeClass.getName() + "Class.cpp");
        CPPFileOutputStream.write(CodeGenerationUtils.generateClassCPP(completeClass).getBytes());
        CPPFileOutputStream.flush();
        CPPFileOutputStream.close();

        OutputStream headerFileOutputStream =
                new FileOutputStream("headers/" + CodeGenerationUtils.generateHeaderName(completeClass.getName()));
        headerFileOutputStream.write(CodeGenerationUtils.generateClassHeaderFile(completeClass).getBytes());
        headerFileOutputStream.flush();
        headerFileOutputStream.close();

        OutputStream CFileOutputStream =
                new FileOutputStream("headers/c_" + completeClass.getName() + ".c");
        CFileOutputStream.write(CodeGenerationUtils.generateClassC(completeClass, allClassNames).getBytes());
        CFileOutputStream.flush();
        CFileOutputStream.close();

        OutputStream infoFileOutputStream =
                new FileOutputStream("diagram_info/" + completeClass.getName() + ".info");
        infoFileOutputStream.write(completeClass.getPhase2Info().getBytes());
        infoFileOutputStream.flush();
        infoFileOutputStream.close();
    }

    public boolean isSuccessFull() {
        return successFull;
    }

}
