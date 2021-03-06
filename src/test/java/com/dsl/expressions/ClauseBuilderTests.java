package com.dsl.expressions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.dsl.Query.*;

public class ClauseBuilderTests {

    // MATCH

    @Test
    void match1() {
        String s = match(
            node("s", "Series").props("power", 1000, "description", "x")
                .left("rel", "Relation").to("a", "Andes"),
            node("m", "Model").props("name", "n")
        ).asString();

        Assertions.assertEquals("MATCH (s:Series {power: 1000, description: x})<-[rel:Relation]-(a:Andes), (m:Model {name: n})", s);
    }

    @Test
    void match2() {
        String s = match(
            node("s", "Series").props("power", 1000, "description", "x")
                .right("rel", "Relation").to("a", "Andes"),
            node("m", "Model").props("name", "n")
        ).asString();

        Assertions.assertEquals("MATCH (s:Series {power: 1000, description: x})-[rel:Relation]->(a:Andes), (m:Model {name: n})", s);
    }

    // WHERE

    @Test
    void where1() {
        String s =
            match(node("s", "Series").props("power", 1000, "description", "x")
                .right("rel", "Relation").to("a", "Andes"), node("m", "Model").props("name", "n")
            ).where(select("s").prop("description").eq("X")
            ).returns(select("s")
            ).asString();

        Assertions.assertEquals("MATCH (s:Series {power: 1000, description: x})-[rel:Relation]->(a:Andes), (m:Model {name: n}) WHERE s.description = 'X' RETURN s", s);
    }

    // WITH

    @Test
    void startingWith() {
        String s = with(literal(1)).returns("A").asString();
        Assertions.assertEquals("WITH 1 RETURN A", s);
    }

    @Test
    void middleWith() {
        String s = match(node("s", "Series")).with(select("s")).returns("s").asString();
        Assertions.assertEquals("MATCH (s:Series) WITH s RETURN s", s);
    }

    @Test
    void multipleWith() {
        String s = match(node("s", "Series"), node("b", "Models"))
            .with(select("s"), select("b"))
            .returns("b", select("s.code"))
            .asString();
        Assertions.assertEquals("MATCH (s:Series), (b:Models) WITH s, b RETURN b, s.code", s);
    }

    @Test
    void aliasedWith() {
        String s = match(node("s", "Series")).with(select("s").as("A")).returns("A").asString();
        Assertions.assertEquals("MATCH (s:Series) WITH s AS A RETURN A", s);
    }

    // RETURN

    @Test
    void returnString() {
        String s = with(literal(1).as("x")).returns("x").asString();
        Assertions.assertEquals("WITH 1 AS x RETURN x", s);
    }

    @Test
    void returnStringAndExpression() {
        String s = with(literal(1).as("x")).returns("x", select("x.name").as("a")).asString();
        Assertions.assertEquals("WITH 1 AS x RETURN x, x.name AS a", s);
    }

    @Test
    void returnExpressionAliased() {
        String s = with(literal(1).as("x")).returns(select("x").as("a")).asString();
        Assertions.assertEquals("WITH 1 AS x RETURN x AS a", s);
    }

    @Test
    void returnExpressionprops() {
        String s = with(literal(1).as("x")).returns(select("x").prop("name")).asString();
        Assertions.assertEquals("WITH 1 AS x RETURN x.name", s);
    }
}
