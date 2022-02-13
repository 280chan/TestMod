package relics;

import java.util.ArrayList;
import java.util.HashMap;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import mymod.TestMod;
import powers.AbstractTestPower;

public class Encyclopedia extends AbstractTestRelic {
	public static final String SAVE_NAME = "Encyclopedia";
	private static final HashMap<AbstractMonster, Integer> CURR = new HashMap<AbstractMonster, Integer>();
	private static final HashMap<String, Integer> SEEN = new HashMap<String, Integer>();
	private static boolean start = false;
	private static boolean victory = false;
	
	public static void clear() {
		SEEN.clear();
		save();
	}
	
	public static void load() {
		SEEN.clear();
		int size = TestMod.getInt(SAVE_NAME);
		for (int i = 1; i <= size; i++)
			SEEN.put(TestMod.getString(SAVE_NAME + (-i)), TestMod.getInt(SAVE_NAME + i));
		victory = start = false;
	}
	
	private static void save() {
		int size;
		TestMod.save(SAVE_NAME, size = SEEN.size());
		ArrayList<String> id = SEEN.keySet().stream().collect(INSTANCE.toArrayList());
		for (int i = 1; i <= size; i++) {
			TestMod.save(SAVE_NAME + (-i), id.get(i - 1));
			TestMod.save(SAVE_NAME + i, SEEN.get(id.get(i - 1)));
		}
	}
	
	public Encyclopedia() {
		super(RelicTier.RARE, LandingSound.SOLID);
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive) {
			clear();
		}
	}
	
	private void put(AbstractMonster m) {
		int c = (int) CURR.keySet().stream().filter(a -> a.id.equals(m.id)).count();
		CURR.put(m, c);
		int amount = SEEN.getOrDefault(m.id, 0) + c;
		if (amount > 0 && m.powers.stream().noneMatch(p -> p instanceof EncyclopediaPower)) {
			m.powers.add(new EncyclopediaPower(m, amount * 50));
		}
	}
	
	private void add(AbstractMonster m) {
		SEEN.computeIfPresent(m.id, (s, i) -> i + 1);
		SEEN.putIfAbsent(m.id, 1);
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		CURR.clear();
		if (AbstractDungeon.getMonsters() != null && AbstractDungeon.getMonsters().monsters != null)
			AbstractDungeon.getMonsters().monsters.forEach(this::put);
		start = true;
    }
	
	public void update() {
		super.update();
		if (!this.isActive)
			return;
		if (start && AbstractDungeon.getMonsters() != null && AbstractDungeon.getMonsters().monsters != null) {
			AbstractDungeon.getMonsters().monsters.stream().filter(not(CURR::containsKey)).forEach(this::put);
		}
	}
	
	public void onVictory() {
		if (!this.isActive)
			return;
		CURR.keySet().forEach(this::add);
		CURR.clear();
		//save();
		start = false;
		victory = true;
    }
	
	@SpirePatch(clz = AbstractRoom.class, method = "update")
	public static class StupidSavePatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(AbstractRoom room) {
			if (victory)
				save();
		}
	}
	
	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(SaveAndContinue.class, "save");
			int[] raw = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			return new int[] { raw[0] + 1 };
		}
	}
	
	public static class EncyclopediaPower extends AbstractTestPower {
		private static final PowerStrings ps = Strings(SAVE_NAME);
		
		public boolean hasThis(AbstractCreature owner) {
			return owner.powers.stream().anyMatch(p -> p instanceof EncyclopediaPower);
		}
		
		public EncyclopediaPower(AbstractCreature owner, int amount) {
			super(SAVE_NAME);
			this.name = ps.NAME;
			this.owner = owner;
			this.amount = amount;
			this.type = PowerType.DEBUFF;
			this.updateDescription();
		}
		
		public void updateDescription() {
			this.description = ps.DESCRIPTIONS[0] + owner.name + ps.DESCRIPTIONS[1]
					+ (single() ? amount : (dmgRate(100f) - 100)) + ps.DESCRIPTIONS[2];
		}
		
		private float dmgRate(float input) {
			return relicStream(Encyclopedia.class).map(r -> get(this::dmg)).reduce(t(), this::chain).apply(input);
		}
		
		private float dmg(float input) {
			return input * (100 + this.amount) / 100;
		}
		
		public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
			return damage < 0 ? damage : dmgRate(damage);
		}

		private boolean single() {
			return relicStream(Encyclopedia.class).count() == 1;
		}
		
		public int onAttacked(DamageInfo info, int damage) {
			return relicStream(Encyclopedia.class).peek(r -> r.show()).mapToInt(r -> damage).findAny().orElse(damage);
		}
		
		public void onRemove() {
			this.addTmpActionToTop(() -> {
				if (!hasThis(this.owner))
					this.owner.powers.add(new EncyclopediaPower(this.owner, this.amount));
			});
		}
		
	}

}