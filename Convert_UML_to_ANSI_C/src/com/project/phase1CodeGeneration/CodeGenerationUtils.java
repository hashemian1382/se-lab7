package com.project.phase1CodeGeneration;

import com.project.classBaseUML.DescriptiveMember;
import org.javatuples.Pair;

import java.util.Vector;

import static com.project.lexicalAnalyzer.CLanguageTokens.*;

public class CodeGenerationUtils {

    public static final String EmptyBlock = newLine + openCurlyBracket + newLine +
            tab + lineComment + whiteSpace + "TODO:code here" + newLine +
            closeCurlyBracket + newLine + newLine;

    public static String generateDestructor(String className)
    {
        String baseClassName = className + destruct + className + openParenthesis + closeParenthesis;
        return baseClassName + EmptyBlock;
    }

    public static String generateHeaderName(String className)
    {
        return className + "Class.h";
    }

    public static String generateIncludeGuard(String headerFile)
    {
        String key = underscore + headerFile.toUpperCase().replace(dot, underscore) + underscore;
        return sharp + ifndef + whiteSpace + key + newLine +
                sharp + defineKeyword + whiteSpace + key + newLine;
    }

    public static String generateEndGuard()
    {
        return sharp + endif + newLine;
    }

    public static String generateIncludeClassHeader(String headerFile)
    {
        return sharp + includeKeyword + whiteSpace + doubleQuotation + headerFile + doubleQuotation + newLine;
    }

    public static String generateOneInlineDefinitionOfClass(String className)
    {
        return unionKeyword + whiteSpace + className + semiColon;
    }

    public static String generateInlineDefinitionOfClass(Vector<String> classNames)
    {
        StringBuilder allLines = new StringBuilder();
        for(String className:classNames)
            allLines.append(generateOneInlineDefinitionOfClass(className)).append(newLine);

        return allLines.toString();
    }

    public static String generateOneUnionUsage(String className)
    {
        return tab + unionKeyword + whiteSpace + className + whiteSpace + unionKeyword + className + semiColon + newLine;
    }

    public static String generateUnionUsage(Vector<String> classNames, String theClassName)
    {
        StringBuilder allLines = new StringBuilder();
        for(String className:classNames)
            if(!className.equals(theClassName))
                allLines.append(generateOneUnionUsage(className));

        return allLines.toString();
    }

    public static String generateAllClassesHeaderFile(Vector<String> classes, String fileName)
    {
        StringBuilder allLines = new StringBuilder();
        allLines.append(generateIncludeGuard(fileName));
        allLines.append(newLine);
        allLines.append(sharp + includeKeyword + whiteSpace + lessThanSign + "stdio.h" + greaterThanSign + newLine);
        allLines.append(sharp + includeKeyword + whiteSpace + lessThanSign + "stdlib.h" + greaterThanSign + newLine);
        allLines.append(newLine);
        allLines.append(generateInlineDefinitionOfClass(classes));
        allLines.append(newLine);
        allLines.append(generateEndGuard());
        return allLines.toString();
    }

    public static String generateIncludesForAClass(Vector<String> classes, String theClassName)
    {
        StringBuilder allLines = new StringBuilder();
        for(String className:classes)
            if(!className.equals(theClassName))
                allLines.append(generateIncludeClassHeader(generateHeaderName(className)));
        return allLines.toString();
    }

    public static String generateUnionDefinitions(CompleteClass completeClass, String className)
    {
        StringBuilder allLines = new StringBuilder();
        allLines.append(unionKeyword + whiteSpace).append(completeClass.getName()).append(newLine)
                .append(openCurlyBracket).append(newLine);
        allLines.append(generateUnionUsage(completeClass.getParents(), className));

        allLines.append(tab + structKeyword + newLine);
        allLines.append(tab + openCurlyBracket + newLine);
        allLines.append(tab + tab).
                append(CompleteAttribute.generateAttributeThisText(className)).append(semiColon).append(newLine);
        for(CompleteAttribute attribute:completeClass.getAllAttributes())
            allLines.append(tab + tab).append(attribute.getShowName()).append(semiColon).append(newLine);
        allLines.append(tab + closeCurlyBracket + semiColon);
        allLines.append(newLine);

        allLines.append(closeCurlyBracket).append(semiColon);
        allLines.append(newLine);
        return allLines.toString();
    }

    public static String generateMethodDefinitions(Vector<Pair<String, CompleteMethod>> methods, String className)
    {
        StringBuilder allLines = new StringBuilder();
        for(Pair<String, CompleteMethod> method:methods)
            allLines.append(method.getValue1().generateMethodDefinition()).append(semiColon).append(newLine);
        return allLines.toString();
    }

    public static String generateConstructorDefinitions(Vector<CompleteConstructor> constructors, String className)
    {
        StringBuilder allLines = new StringBuilder();
        for(CompleteConstructor constructor:constructors)
        {
            allLines.append(constructor.generateConstructorDefinition()).append(semiColon).append(newLine);
            allLines.append(constructor.generateNewDefinition());
        }
        return allLines.toString();
    }

    public static String generateOverloadFile(Vector<String> classes, String fileName) {
        StringBuilder allLines = new StringBuilder();
        allLines.append(generateIncludeGuard(fileName));
        allLines.append(newLine);
        allLines.append(sharp + includeKeyword + whiteSpace + lessThanSign + "stdio.h" + greaterThanSign + newLine);
        allLines.append(sharp + includeKeyword + whiteSpace + lessThanSign + "stdlib.h" + greaterThanSign + newLine);
        for(String className:classes)
            allLines.append(generateIncludeClassHeader(generateHeaderName(className)));
        allLines.append(newLine);
        allLines.append(MethodOverloader.generateOverloadMacros());
        allLines.append(newLine);
        allLines.append(newLine);
        allLines.append(generateEndGuard());

        return allLines.toString();

    }

    public static String generateClassC(CompleteClass completeClass, Vector<String> classes) {
        StringBuilder allLines = new StringBuilder();
        allLines.append(generateIncludeClassHeader("overload.h"));
        allLines.append(newLine);
        allLines.append(generateMethodBody(completeClass.getAllMethodsBasedOnParents(), completeClass.getName()));
        allLines.append(newLine);
        allLines.append(generateConstructorBody(completeClass.getConstructors(), completeClass.getName()));
        allLines.append(newLine);
        if(completeClass.isHavingDestructor())
        {
            allLines.append(completeClass.generateDeleteBody());
            allLines.append(newLine);
        }

        return allLines.toString();
    }

    private static String generateConstructorBody(Vector<CompleteConstructor> constructors, String className) {
        StringBuilder allLines = new StringBuilder();
        for(CompleteConstructor constructor:constructors)
            allLines.append(generateNewBody(constructor, className));
        return allLines.toString();
    }

    private static String generateNewBody(CompleteConstructor constructor, String className) {
        StringBuilder allLines = new StringBuilder();
        allLines.append(constructor.generateConstructorReturnValue()).append(whiteSpace)
                .append(constructor.generateNewName())
                .append(constructor.getShowName());
        allLines.append(newLine + openCurlyBracket + newLine);
        allLines.append(tab).append(CompleteAttribute.generateAttributeThisText(className))
                .append(whiteSpace + equalSign + whiteSpace)
                .append(openParenthesis).append(constructor.generateConstructorReturnValue()).append(closeParenthesis)
                .append(whiteSpace + mallocKeyword + openParenthesis +
                        sizeofKeyword + openParenthesis + unionKeyword + whiteSpace)
                .append(className).append(closeParenthesis + closeParenthesis + semiColon + newLine);
        allLines.append(tab).append(constructor.generateConstructorName()).append(openParenthesis + thisKeyword);
        for(CompleteAttribute attribute:constructor.getParams())
            allLines.append(comma + whiteSpace).append(attribute.getName());
        allLines.append(closeParenthesis + semiColon + newLine);
        allLines.append(tab + returnKeyword + whiteSpace + thisKeyword + semiColon + newLine);
        allLines.append(closeCurlyBracket + newLine + newLine);

        return allLines.toString();
    }

    private static String generateMethodBody(Vector<Pair<String, CompleteMethod>> methods, String className) {
        StringBuilder allLines = new StringBuilder();
        for(Pair<String, CompleteMethod> method:methods)
        {
            if(!method.getValue0().equals(className))
            {
                allLines.append(method.getValue1().generateMethodDefinition());
                allLines.append(newLine);
                allLines.append(method.getValue1().generateMethodUseInDefinition(method.getValue0()));
            }
        }
        return allLines.toString();
    }

    public static String generateClassHeaderFile(CompleteClass completeClass)
    {
        StringBuilder allLines = new StringBuilder();
        String fileName = generateHeaderName(completeClass.getName());
        allLines.append(generateIncludeGuard(fileName));
        allLines.append(newLine);
        allLines.append(generateIncludeClassHeader("AllClasses.h"));
        allLines.append(newLine);
        allLines.append(generateIncludesForAClass(completeClass.getParents(), completeClass.getName()));
        allLines.append(newLine);
        allLines.append(newLine);

        allLines.append(generateUnionDefinitions(completeClass, completeClass.getName()));
        allLines.append(newLine);
        allLines.append(
                generateMethodDefinitions(completeClass.getAllMethodsBasedOnParents(), completeClass.getName()));
        allLines.append(newLine);
        allLines.append(generateConstructorDefinitions(completeClass.getConstructors(), completeClass.getName()));
        allLines.append(newLine);
        if(completeClass.isHavingDestructor())
        {
            allLines.append(completeClass.generateDestructorDefinition());
            allLines.append(completeClass.generateDeleteDefinition());
            allLines.append(newLine);
        }

        allLines.append(generateEndGuard());
        return allLines.toString();
    }

    public static String generateClassCPP(CompleteClass completeClass)
    {
        StringBuilder base = new StringBuilder();
        base.append(generateIncludeClassHeader("overload.h"));
        for(CompleteConstructor constructor:completeClass.getConstructors())
            base.append(constructor.generateConstructor());
        for(CompleteMethod method:completeClass.getMethods())
            base.append(method.generateMethod());
        if(completeClass.isHavingDestructor())
            base.append(generateDestructor(completeClass.getName()));
        return base.toString();
    }

}
