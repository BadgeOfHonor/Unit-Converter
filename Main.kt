package converter

import kotlin.system.exitProcess

fun main() {
    Converter()
}
enum class UnitType { Length, Weight, Temperature, NULL }

enum class Unit(
    val type: UnitType,
    val unitToNorma: (Double) -> Double,
    val normaToUnit: (Double) -> Double,
    val variants: List<String>
    ) {
    M    (UnitType.Length,       { v -> v },                    { v -> v },                  listOf("m", "meter", "meters")),
    KM   (UnitType.Length,       { v -> v * 1000 },             { v -> v / 1000 },           listOf("km", "kilometer", "kilometers")),
    CM   (UnitType.Length,       { v -> v * 0.01 },             { v -> v / 0.01 },           listOf("cm", "centimeter", "centimeters")),
    MM   (UnitType.Length,       { v -> v * 0.001 },            { v -> v / 0.001 },          listOf("mm", "millimeter", "millimeters")),
    MI   (UnitType.Length,       { v -> v * 1609.35 },          { v -> v / 1609.35 },        listOf("mi", "mile", "miles")),
    YD   (UnitType.Length,       { v -> v * 0.9144 },           { v -> v / 0.9144 },         listOf("yd", "yard", "yards")),
    FT   (UnitType.Length,       { v -> v * 0.3048 },           { v -> v / 0.3048 },         listOf("ft", "foot", "feet")),
    IN   (UnitType.Length,       { v -> v * 0.0254 },           { v -> v / 0.0254 },         listOf("in", "inch", "inches")),
    G    (UnitType.Weight,       { v -> v },                    { v -> v },                  listOf("g", "gram", "grams")),
    KG   (UnitType.Weight,       { v -> v * 1000.0 },           { v -> v / 1000.0 },         listOf("kg", "kilogram", "kilograms")),
    MG   (UnitType.Weight,       { v -> v * 0.001 },            { v -> v / 0.001 },          listOf("mg", "milligram", "milligrams")),
    LB   (UnitType.Weight,       { v -> v * 453.592 },          { v -> v / 453.592 },        listOf("lb", "pound", "pounds")),
    OZ   (UnitType.Weight,       { v -> v * 28.3495 },          { v -> v / 28.3495 },        listOf("oz", "ounce", "ounces")),
    C    (UnitType.Temperature,  { v -> v + 273.15 },           { v -> v - 273.15 },         listOf("dc", "degree Celsius", "degrees Celsius", "celsius", "c")),
    F    (UnitType.Temperature,  { v -> (v + 459.67) * 5 / 9 }, { v -> v * 9 / 5 - 459.67 }, listOf("df", "degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "f")),
    K    (UnitType.Temperature,  { v -> v },                    { v -> v },                  listOf("k", "kelvin", "kelvins")),
    NULL (UnitType.NULL,         { v -> v * 0.0 },              { v -> v * 0.0 },            listOf("???", "???", "???"));

    companion object {

        fun findUnit(_variant: String): Unit? {
            val variant = _variant.lowercase()
            for (enum in values()) {
                 if (variant in enum.variants) return enum
            }
            return null
        }

    }

}

class Converter {

    private var result: Double = 0.0
    private var inputNumber: Double = 0.0
    private var inputMeasure: String = ""
    private var inputUnit: Unit = Unit.NULL
    private var resultMeasure: String = ""
    private var resultUnit: Unit = Unit.NULL

    val convertErrorCheck = { _inputNumber: String, _inputMeasure: String, _resultMeasure: String ->
        val inputNumber = _inputNumber.toDouble()
        inputUnit = Unit.findUnit(_inputMeasure) ?: Unit.NULL
        resultUnit = Unit.findUnit(_resultMeasure) ?: Unit.NULL

        if (inputNumber < 0 && inputUnit.type != UnitType.Temperature && inputUnit.type != UnitType.NULL)
            throw Exception("${inputUnit.type.name} shouldn't be negative\n")

        if (inputUnit == Unit.NULL || resultUnit == Unit.NULL || inputUnit.type != resultUnit.type) {
          throw Exception("Conversion from ${inputUnit.variants[2]} to ${resultUnit.variants[2]} is impossible\n")
        }
    }

    val checkParsing = { userResponse: String ->
        if (!userResponse.lowercase().matches(("\\+?-?\\d+\\.?\\d*\\s((degrees\\s\\w+|degree\\s\\w+)|\\w+)" +
                    "\\s\\w+\\s((degrees\\s\\w+|degree\\s\\w+)|\\w+)").toRegex()))
            throw Exception("Parse error\n")
    }

    val singularPlural = { number: Double, unit: Unit -> if (number == 1.0) unit.variants[1] else unit.variants[2] }

    init {
        run()
    }

    private fun run() {
        while (true) {
            print("Enter what you want to convert (or exit): ")
            val userResponse = readln()
            if (userResponse.lowercase() == "exit") exitProcess(0)
            try { checkParsing(userResponse) } catch (e: Exception) { println(e.message); continue }
            val (_input, _inputMeasure, _, _resultMeasure) = userResponse.lowercase().split(" degree ", " degrees ", " ")
            try { convertErrorCheck(_input, _inputMeasure, _resultMeasure) }
            catch (e: Exception) { println(e.message); continue }
            inputNumber = _input.toDouble()
            inputMeasure = singularPlural(inputNumber, inputUnit)
            result = convert(inputNumber)
            resultMeasure = singularPlural(result, resultUnit)
            println(this.toString())
        }
    }

    override fun toString(): String  {
        return "$inputNumber $inputMeasure is $result $resultMeasure\n"
    }

    private fun convert(
        value: Double,
        unitToNorma: (Double) -> Double = inputUnit.unitToNorma,
        normaToUnit: (Double) -> Double = resultUnit.normaToUnit
    ): Double {
        return  normaToUnit(unitToNorma(value))
    }
}