package com.ignoransel.whimsicalideas.content.soulsail;

public final class SoulSailKeys {
    private SoulSailKeys() {}

    public static final String SOULS = "Souls";               // long
    public static final String RAW_SOULS = "RawSouls";           // long 未炼化
    public static final String REFINED_SOULS = "RefinedSouls";   // long 已炼化
    public static final String STORED = "StoredMobs";         // NbtList<String>：待生成
    public static final String ROOM_X = "RoomX";              // int
    public static final String ROOM_Z = "RoomZ";              // int
    public static final String ACTIVE = "Active"; // boolean
    public static final String BANNER_GRADE = "BannerGrade";        // int (0-9) 品阶等级
    public static final String LAST_RADIUS = "LastRadius"; // int
    public static final String SAIL_ID = "SailId"; // String UUID

    // 返回点（进入魂帆世界前的位置）
    public static final String RETURN_DIM = "ReturnDim";      // String (registry key like "minecraft:overworld")
    public static final String RETURN_X = "ReturnX";          // double
    public static final String RETURN_Y = "ReturnY";          // double
    public static final String RETURN_Z = "ReturnZ";          // double
    public static final String RETURN_YAW = "ReturnYaw";      // float
    public static final String RETURN_PITCH = "ReturnPitch";  // float


    // 技能相关
    public static final String SELECTED_ABILITY = "SelectedAbility"; // int (枚举 ordinal)
    public static final String ABILITY_CDS = "AbilityCooldowns"; // Compound：{ "HEAL": long, "SOUL_TOTEM": long ... }
    public static final String PASSIVE_SOUL_TOTEM = "PassiveSoulTotem";
    public static final String PASSIVE_SOUL_BARRIER = "PassiveSoulBarrier"; // boolean
    public static final String PASSIVE_SOUL_DOMAIN = "PassiveSoulDomain";
    public static final String GRASP_UNTIL = "GraspUntil";   // long
    public static final String GRASP_TARGET = "GraspTarget"; // uuid string

    public static final String MAELSTROM_UNTIL = "MaelstromUntil"; // long
    public static final String MAELSTROM_X     = "MaelstromX";     // double
    public static final String MAELSTROM_Y     = "MaelstromY";     // double
    public static final String MAELSTROM_Z     = "MaelstromZ";     // double
    public static final String MAELSTROM_START = "MaelstromStart"; // long

    public static final String JUDGMENT_ACTIVE = "JudgmentActive";
    public static final String JUDGMENT_START  = "JudgmentStart";
    public static final String JUDGMENT_UNTIL  = "JudgmentUntil";
    public static final String JUDGMENT_X      = "JudgmentX";
    public static final String JUDGMENT_Y      = "JudgmentY";
    public static final String JUDGMENT_Z      = "JudgmentZ";

    // ⚡ 雷罚名单（触碰边界后加入，持续雷劈到死）
    public static final String JUDGMENT_PUNISHED = "JudgmentPunished";


}
