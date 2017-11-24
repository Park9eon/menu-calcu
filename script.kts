import java.io.File
import java.io.PrintWriter

/**
 * Initial version by: park9eon
 * Initial version created on: 24/11/2017
 */
// 참석 여부


// 사건 발생 n회
val COUNT_TEST = 1000
// 참석 여부 확정
val COUNT_GOING = 22
// 참석 여부 미확정
val COUNT_MIGHT = 6


var total = 0

enum class Attend(
        val prob: Float // 참석 확률
) {
    GOING(0.9F), MAYBE(0.5F), NOT(0.0F);

    fun joined(): Boolean = Math.random() <= prob
}

enum class MenuType {
    MEAT, SAKE, ETC
}

data class Member(val name: String, var attend: Attend) {
    init {
        if (attend.joined()) {
            attend = Attend.GOING
        } else {
            attend = Attend.NOT
        }
    }

    fun selectMenu(): List<Menu> {
        return listOf(
                // 1~2회
                *(1..(Math.random() * 2.0 + 1).toInt()).map {
                    Menu.MAIN_MENU_LIST[(Math.random() * Menu.MAIN_MENU_LIST.size).toInt()]
                }.toTypedArray(),
                // 0~2회
                *(1..(Math.random() * 2.0).toInt()).map {
                    Menu.DRINK_MENU_LIST[(Math.random() * Menu.DRINK_MENU_LIST.size).toInt()]
                }.toTypedArray(),
                // 0~2회
                *(1..(Math.random() * 2.0).toInt()).map {
                    Menu.SIDE_MENU_LIST[(Math.random() * Menu.SIDE_MENU_LIST.size).toInt()]
                }.toTypedArray()
        )
    }
}

data class Menu(val type: MenuType, val name: String, val price: Int) {
    companion object {
        val MAIN_MENU_LIST = listOf(
                Menu(MenuType.MEAT, "돼지", 9000),
                Menu(MenuType.MEAT, "소", 11000)
        )
        val DRINK_MENU_LIST = listOf(
                Menu(MenuType.SAKE, "소주", 4000),
                Menu(MenuType.SAKE, "맥주", 6000)
        )
        val SIDE_MENU_LIST = listOf(
                Menu(MenuType.ETC, "밥", 1000),
                Menu(MenuType.ETC, "국물", 3000)
        )
    }

    override fun toString(): String {
        return this.name
    }
}

// 결과를 가시화
fun event(): Pair<Int, String> {

    val goingMemberList = (1..COUNT_GOING).map {
        Member("참석 확정 ${it}님", Attend.GOING)
    }
    val mightMemberList = (1..COUNT_MIGHT).map {
        Member("참석 미확정 ${it}님", Attend.MAYBE)
    }

    val joinedMemberList = listOf(*goingMemberList.toTypedArray(), *mightMemberList.toTypedArray())
            .filter {
                it.attend == Attend.GOING
            }
    val totalSelectedMenu = joinedMemberList.map {
        it.selectMenu().apply {
            println("참석자가 메뉴를 골랐습니다. [${this.joinToString(",")}]를 먹었습니다. ${this.sumBy { it.price }}원")
        }
    }.flatten()

    val sum = totalSelectedMenu.sumBy { it.price }
    return Pair(sum / joinedMemberList.size,
"""
${joinedMemberList.size} 명이 참석하여
[${
            totalSelectedMenu.groupBy { it.name }
                    .map {
                        "${it.key} - ${it.value.size}개"
                    }
                    .joinToString(", ")
            }]

총 ${sum}원
""")
}

File("README.MD").printWriter().use { out ->
    // n회 테스트
    for (count in 1..COUNT_TEST) {
        out.println("NEW Event!!")
        out.println("---------------------")
        val event = event()
        out.println(event.second)
        total += event.first
    }
    out.println("결과")
    out.println("---------------------")
    out.println("총 ${COUNT_TEST}회 실행결과 평균 회비 ${total / COUNT_TEST}원이 필요!")
}