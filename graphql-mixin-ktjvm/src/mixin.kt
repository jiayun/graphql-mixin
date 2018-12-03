import java.io.File

fun main(args: Array<String>) {
//    args.forEach { println(it) }

    val schema = readAndCombineFiles(args)
//    println(schema)

    val (schema2, mixins) = extractMixinDefinitions(schema)
    val schema3 = injectMixin(schema2, mixins)

    println(schema3)
}

fun readAndCombineFiles(filenames: Array<String>): String {
    val sb = StringBuilder()

    filenames.forEach {
        sb.append(File(it).readText())
        sb.append("\n")
    }

    return sb.toString()
}

fun extractMixinDefinitions(schema: String): Pair<String, Map<String, String>> {
    val map = HashMap<String, String>()
    val regex = """(^#.*?\n)*\s*mixin\s*(\w*)\s*\{\s(^[^}]*)\s^}""".toRegex(RegexOption.MULTILINE)

    var matchResult = regex.find(schema)

    while (matchResult != null) {
//        println(matchResult.value)

        val (comment, name, definition) = matchResult.destructured
        map[name] = definition
//        println("comment: $comment\nname: $name\ndefinition:\n$definition")

        matchResult = matchResult.next()
    }

    val result = regex.replace(schema, "")
//    println(result)

    return Pair(result, map)
}

fun injectMixin(schema: String, definitions: Map<String, String>): String {
    val regex = """\s*?@mixin\s(\w*).*""".toRegex()

    var matchResult = regex.find(schema)

    while (matchResult != null) {
//        println(matchResult.value)
        matchResult = matchResult.next()
    }

    return regex.replace(schema) {
        val (name) = it.destructured
//        println("mixin name: $name")
//        println(definitions[name]!!)
        "\n" + definitions[name]!!
    }
}
