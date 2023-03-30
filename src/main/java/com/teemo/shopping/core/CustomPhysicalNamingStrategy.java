package com.teemo.shopping.core;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;


//todo: identifier 가 null 인 문제 해결
public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {
    @Override
    public Identifier toPhysicalCatalogName(Identifier logicalName,
        JdbcEnvironment jdbcEnvironment) {
        if(logicalName == null) logicalName = Identifier.toIdentifier("default");
        return prefix("catalog_", covertToSnakeCase(logicalName));
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier logicalName,
        JdbcEnvironment jdbcEnvironment) {
        if(logicalName == null) logicalName = Identifier.toIdentifier("default");
        return prefix("schema_", covertToSnakeCase(logicalName));
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return simpleConvertSingularToPlural(covertToSnakeCase(logicalName));
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier logicalName,
        JdbcEnvironment jdbcEnvironment) {
        return prefix("sequence_", covertToSnakeCase(logicalName));
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier logicalName,
        JdbcEnvironment jdbcEnvironment) {
        return covertToSnakeCase(logicalName);
    }

    public Identifier covertToSnakeCase(Identifier identifier) {
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText()
            .replaceAll(regex, replacement)
            .toLowerCase();
        return Identifier.toIdentifier(newName);
    }
    public Identifier prefix(String prefix, Identifier identifier) {
        return Identifier.toIdentifier(prefix.concat(identifier.getText()));
    }
    public Identifier simpleConvertSingularToPlural(Identifier identifier) {

        String[] regexes = {
            "[i|e|o|a|u]y$",    //y 에외
            "y$",
            "([s|x|z|sh|ch])$",
            "is$",
            "us$",
            "$"
        };
        String[] replacements = {
            "s",
            "ies",
            "$1es",
            "es",
            "i",
            "s"
        };
        String identifierStr = identifier.getText();
        int length = regexes.length;
        String newStr = null;
        for(int i = 0; i < length; i++) {
            newStr = identifierStr.replaceAll(regexes[i], replacements[i]);
            if(!newStr.equals(identifierStr)) break;
        }
        return Identifier.toIdentifier(newStr);
    }
}
