external val process: dynamic
external fun require(module: String): dynamic

fun main(args: Array<String>) {
    val result = mixin((process["argv"] as Array<String>).drop(2).toTypedArray())
    println(result)
}

@JsName("mixin")
fun mixin(args: Array<String>): String {
//    args.forEach { println(it) }

    val schema = readAndCombineFiles(args)
//    println(schema)

    val (schema2, mixins) = extractMixinDefinitions(schema)
    return injectMixin(schema2, mixins)
}

fun readAndCombineFiles(filenames: Array<String>): String {
    val sb = StringBuilder()
    val readFileSync = require("fs").readFileSync

    filenames.forEach {
        val test = readFileSync(it)
        sb.append(test.toString())
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

        val (_, name, definition) = matchResult.destructured
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
