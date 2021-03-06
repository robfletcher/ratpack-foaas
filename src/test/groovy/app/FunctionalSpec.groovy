package app

import org.ratpackframework.test.ScriptAppSpec
import spock.lang.Unroll

@Unroll
class FunctionalSpec extends ScriptAppSpec {

    def "content negotiation"() {
        when:
        request.header("Accept", "*/*")

        then:
        getText("off/to/from") == "Fuck off, to. - from"

        when:
        request.header("Accept", "application/json")

        then:
        with(get("off/to/from").jsonPath()) {
            get("message") == "Fuck off, to."
            get("subtitle") == "- from"
        }

        when:
        request.header("Accept", "text/html")

        then:
        with(get("off/to/from")) {
            contentType == "text/html;charset=UTF-8"
            body.asString().contains "Fuck off, to."
        }
    }

    def "handles unknowns"() {
        when:
        get("foo/bar/baasdfa")

        then:
        response.statusCode == 404
    }

    def "pathtokens are properly encoded"() {
        when:
        request.header("Accept", "text/html")

        then:
        with(get("blink182/generation/fucking%20heart")) {
            !body.asString().contains("%20")
        }
    }

	def "variant text is produced by #path"() {
		when:
		request.header("Accept", "*/*")

		then:
		getText(path) == expectedText

		where:
		path                   | expectedText
		"keepcalm/to/from"     | "Keep Calm to and Fuck Off - from"
		"steviewonder/to/from" | "I just called, to say..........to FUCK OFF! - from"
		"malcolmtucker/from"   | "I will tear your fucking skin off, I will wear it to your mother's birthday party and rub your nuts up and down her leg whilst whistling Bohemian fucking Rhapsody. Right? - from"
		"swearengen/E.B./Al"   | "I will profane your fucking remains, E.B.! - Al"
	}
}
