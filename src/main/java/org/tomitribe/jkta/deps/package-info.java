/**
 * Maven Central and Bytecode Analysis
 *
 * This package contains code used to analyze the references between
 * each Jakarta EE API.
 *
 * This code will:
 *
 *  - walk the `jakarta` groupId in Maven Central
 *  - download each jar and cache it locally
 *  - extract each jar into a temp directory
 *  - parse the bytecode code via ASM
 *
 *  All the above has been done and a jakartaee-classes.json file exists
 *  in this repo and can be used to do analysis, skipping the above steps.
 */
package org.tomitribe.jkta.deps;