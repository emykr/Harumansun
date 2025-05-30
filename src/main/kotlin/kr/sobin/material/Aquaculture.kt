package kr.sobin.material

interface Aquaculture {
    /**
     * Represents the different types of materials used in aquaculture.
     */
    enum class Material(val id: String) {
        ATLANTIC_COD("aquaculture:atlantic_cod"),
        BLACKFISH("aquaculture:blackfish"),
        PACIFIC_HALIBUT("aquaculture:pacific_halibut"),
        ATLANTIC_HERRING("aquaculture:atlantic_herring"),
        PINK_SALMON("aquaculture:pink_salmon"),
        POLLOCK("aquaculture:pollock"),
        RAINBOW_TROUT("aquaculture:rainbow_trout"),
        BAYAD("aquaculture:bayad"),
        BOULTI("aquaculture:boulti"),
        CAPITAIN("aquaculture:capitain"),
        SYNDONTIS("aquaculture:syndontis"),
        SMAILLMOUTH_BASS("aquaculture:smallmouth_bass"),
        BLUEGILL("aquaculture:bluegill"),
        BROWN_TROUT("aquaculture:brown_trout"),
        CARP("aquaculture:carp"),
        CATIFSH("aquaculture:catfish"),
        GAR("aquaculture:gar"),
        MINNOW("aquaculture:minnow"),
        MUSKELLUNGE("aquaculture:muskellunge"),
        PERCH("aquaculture:perch"),
        ARAPAIMA("aquaculture:arapaima"),
        PIRANHA("aquaculture:piranha"),
        TAMBAQUI("aquaculture:tambaqui"),
        BROWN_SHROOMA("aquaculture:brown_shrooma"),
        RED_SHROOMA("aquaculture:red_shrooma"),
        RED_GROUPER("aquaculture:red_grouper"),
        TUNA("aquaculture:tuna"),
        GOLDFISH("aquaculture:goldfish"),
        BOX_TURTLE("aquaculture:box_turtle"),
        ARRARU_TURTLE("aquaculture:arraru_turtle"),
        STARSHELL_TURTLE("aquaculture:starshell_turtle");
    }
}

