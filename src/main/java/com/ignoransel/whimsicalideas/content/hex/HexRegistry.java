package com.ignoransel.whimsicalideas.content.hex;

import com.ignoransel.whimsicalideas.content.hex.hexs.*;

import java.util.*;
import java.util.function.Supplier;

public final class HexRegistry {
    private HexRegistry() {}

    private static final Map<String, Supplier<? extends HexBase>> BY_ID = new HashMap<>();
    private static final Map<HexRarity, List<Supplier<? extends HexBase>>> POOLS = new EnumMap<>(HexRarity.class);

    static {
        // 初始化每个稀有度池
        for (HexRarity r : HexRarity.values()) {
            POOLS.put(r, new ArrayList<>());
        }

        // ===== 在这里注册所有 Hex =====
        // 铁池
        register(HexVitality::new);
        register(HexAgility::new);
        register(HexMight::new);

        // 金池
        register(HexMaxMining::new);
        register(HexRefine::new);
        register(HexRegeneration::new);

        // 钻石池
        // register(HexSomethingDiamond::new);

        // 下界合金池
        // register(HexSomethingNetherite::new);
    }

    private static void register(Supplier<? extends HexBase> factory) {
        HexBase sample = factory.get();
        BY_ID.put(sample.getId(), factory);
        POOLS.get(sample.getRarity()).add(factory);
    }

    /** 用 id 获取一个“新实例” */
    public static HexBase getById(String id) {
        Supplier<? extends HexBase> factory = BY_ID.get(id);
        return factory == null ? null : factory.get();
    }

    /** 只从某个稀有度池里随机一个“新实例” */
    public static HexBase randomFromRarity(HexRarity rarity, Random random) {
        List<Supplier<? extends HexBase>> pool = POOLS.get(rarity);
        if (pool == null || pool.isEmpty()) return null;
        return pool.get(random.nextInt(pool.size())).get();
    }
}
