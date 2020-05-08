/**
 * Github Project and Source Code Analysis
 *
 * This package contains code used to analyze the ability to split
 * certain EE4J repos out into the jakartaee org.
 *
 * This code will:
 *
 *  - walk the eclipse-ee4j org in Github via the Github API
 *  - clone each repo via jgit
 *  - parse the source code via JavaParser
 *
 *  A command line version of this is provided, but not really needed
 */
package org.tomitribe.jkta.repos;