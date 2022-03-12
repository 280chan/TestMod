package utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Careless;
import com.megacrit.cardcrawl.daily.mods.ControlledChaos;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;

import actions.AbstractXCostAction;
import basemod.BaseMod;
import basemod.Pair;
import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;
import mymod.TestMod;
import powers.AbstractTestPower;
import relics.AbstractTestRelic;

@SuppressWarnings({ "unchecked", "rawtypes" })
public interface MiscMethods {
	public static final MiscMethods INSTANCE = new MiscMethods() {};
	
	default void print(Object s) {
		TestMod.info(s);
	}
	
	static String getIDWithoutLog(Class<?> c) {
		try {
			c.getDeclaredField("ID");
		} catch (NoSuchFieldException | SecurityException e) {
			return c.getSimpleName();
		}
		return ReflectionHacks.getPrivateStatic(c, "ID");
	}

	static String getIDForUI() {
		try {
			return Class.forName(Stream.of(new Exception().getStackTrace()).map(i -> i.getClassName())
					.filter(s -> !"utils.MiscMethods".equals(s)).findFirst().get()).getSimpleName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	default UIStrings uiString() {
		return uiString(getIDForUI());
	}
	
	default UIStrings uiString(String id) {
		return CardCrawlGame.languagePack.getUIString(TestMod.makeID(id));
	}
	
	default void addEnergy() {
		p().energy.energyMaster++;
	}
	
	default void reduceEnergy() {
		p().energy.energyMaster--;
	}
	
	default void addPower(AbstractPower p) {
		p().powers.add(p);
	}
	
	default void removePower(AbstractPower p) {
		p().powers.remove(p);
	}
	
	default int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}
	
	public default int getDate() {
		return Calendar.getInstance().get(Calendar.DATE);
	}
	
	public default AbstractPlayer p() {
		return AbstractDungeon.player;
	}
	
	public default <T> ArrayList<T> getIdenticalList(T value, int size) {
		ArrayList<T> list = new ArrayList<T>();
		while (list.size() < size)
			list.add(value);
		return list;
	}
	
	public default ArrayList<Integer> getNaturalNumberList(int n) {
		return getNumberList(0, n);
	}
	
	public default ArrayList<Integer> getNumberList(int start, int end) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int s = end > start ? 1 : -1;
		for (int i = start; (end - i) * s > 0; i += s)
			list.add(i);
		return list;
	}
	
	public default void togglePulse(AbstractRelic r, boolean pulse) {
		Lambda f = pulse ? r::beginLongPulse : r::stopPulse;
		f.run();
	}
	
	public default void markAsSeen(AbstractRelic r) {
		if (!UnlockTracker.isRelicSeen(r.relicId)) {
			UnlockTracker.markRelicAsSeen(r.relicId);
			TestMod.info("成功解锁了未见过的 " + r.name);
		}
	}
	
	public default void markAsSeen(AbstractCard c) {
		if (!UnlockTracker.isCardSeen(c.cardID)) {
			UnlockTracker.markCardAsSeen(c.cardID);
			TestMod.info("成功解锁了未见过的 " + c.name);
		}
	}
	
	public default CardGroup getSource(AbstractCard c) {
		return Stream.of(p().discardPile, p().hand, p().drawPile).filter(g -> g.contains(c)).findAny().orElse(null);
	}
	
	public default boolean handFull() {
		return p().hand.size() >= BaseMod.MAX_HAND_SIZE;
	}
	
	public default boolean isLocalTesting() {
		return TestMod.isLocalTest();
	}
	
	public default void setXCostEnergy(AbstractCard c) {
		if (c.cost == -1)
			c.energyOnUse = EnergyPanel.totalCount;
	}
	
	public default boolean hasPrudence() {
		return p().hasRelic(TestMod.makeID("Prudence"));
	}
	
	public default boolean hasStringDisintegrator() {
		return p().hasRelic(TestMod.makeID("StringDisintegrator"));
	}
	
	public default void addHoarderCard(CardGroup g, AbstractCard c) {
		if (ModHelper.isModEnabled("Hoarder")) {
			addExtraCard(g, c, 2);
		}
	}
	
	public default void addExtraCard(CardGroup g, AbstractCard c, int num) {
		this.getIdenticalList(0, num).forEach(i -> g.addToTop(c.makeStatEquivalentCopy()));
	}
	
	static class IdChecker {
		String id;
		public IdChecker(String id) {
			this.id = id;
		}
		public boolean check(AbstractCard c) {
			return this.id.equals(c.cardID);
		}
		public boolean check(AbstractRelic r) {
			return this.id.equals(r.relicId);
		}
	}
	
	public default ArrayList<AbstractCard> getAllInBattleInstance(String cardID) {
		IdChecker checker = new IdChecker(cardID);
		AbstractPlayer p = p();
		return Stream
				.concat(Stream.of(p.drawPile, p.discardPile, p.exhaustPile, p.limbo, p.hand)
						.flatMap(g -> g.group.stream()), Stream.of(p.cardInUse))
				.filter(checker::check).collect(this.toArrayList());
	}
	
	public default void turnSkipperStart() {
    	TurnSkipper.start();
    } 
	
	public default void turnSkipperStartByCard(AbstractCard c) {
		TurnSkipper.startByCard(c);
    }
	
	public default void turnSkipperUpdate() {
		TurnSkipper.updateThis();
	}
	
	static class TurnSkipper {
		private static boolean startEndingTurn = false;
	    private static boolean endTurnQueued = false;
	    private static boolean startMonsterTurn = false;
		private static boolean startNextTurn = false;

		private static boolean inProgress() {
			return startEndingTurn || endTurnQueued || startMonsterTurn || startNextTurn;
		}
		
	    public static void start() {
	    	if (inProgress())
				return;
	    	INSTANCE.addTmpActionToBot(() -> {
				AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
				AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
				startEndingTurn = true;
			});
		}
	    
	    public static void startByCard(AbstractCard c) {
	    	if (inProgress())
	    		return;
			start();
			INSTANCE.addTmpActionToBot(() -> {
				if (!c.dontTriggerOnUseCard)
					for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
						boolean timecollector = false;
						int tcIndex = -1;
						for (AbstractPower p : monster.powers) {
							if (p.ID.equals("Time Warp") && p.amount == 11) {
								startByCardTimeWarpIssues(p);
							}
							if (p.ID.equals("ImpatiencePower") && p.amount == 14) {
								p.amount = -1;
							}
							if (p.ID.equals("TimeCollector") && p.amount == 11) {
								timecollector = true;
								tcIndex = monster.powers.indexOf(p);
							}
						}
						if (timecollector && tcIndex > -1) {
							monster.powers.remove(tcIndex);
						}
					}
			});
	    }
	    
	    private static void startByCardTimeWarpIssues(AbstractPower p) {
	    	p.amount = -1;
			p.playApplyPowerSfx();
			CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
			AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
			AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
			AbstractDungeon.getMonsters().monsters
					.forEach(m -> addToBot(new ApplyPowerAction(m, m, new StrengthPower(m, 2), 2)));
	    }
	    
	    private static void updateStartEndingTurn() {
	    	if (!startEndingTurn)
	    		return;
	    	startEndingTurn = false;
			AbstractDungeon.actionManager.cardQueue.stream().filter(i -> i.autoplayCard).forEach(i -> {
				i.card.dontTriggerOnUseCard = true;
				addToBot(new UseCardAction(i.card));
			});
	    	AbstractDungeon.actionManager.cardQueue.clear();
	    	
	    	INSTANCE.p().limbo.group.forEach(c -> AbstractDungeon.effectList.add(new ExhaustCardEffect(c)));
	    	
	    	TestMod.info("limbo数量 = " + INSTANCE.p().limbo.size());
	    	INSTANCE.p().limbo.group.clear();
	    	INSTANCE.p().releaseCard();
	        // Start Ending Turn
	        
	        addToBot(new NewQueueCardAction());
	        EndTurnButton etb = AbstractDungeon.overlayMenu.endTurnButton;
	        etb.enabled = false;
	        etb.isGlowing = false;
	        etb.updateText(EndTurnButton.ENEMY_TURN_MSG);
	        CardCrawlGame.sound.play("END_TURN");
	        INSTANCE.p().releaseCard();
	        // EndTurnButton Effect
	        
	        endTurnQueued = true;
	    }
	    
	    public static void updateThis() {
	    	updateStartEndingTurn();
	    	updateEndTurnQueued();
			updateMonsterTurn();
			updateStartNextTurn();
	    }
	    
	    private static void updateEndTurnQueued() {
	    	if (!AbstractDungeon.actionManager.actions.isEmpty())
	        	return;
			if (endTurnQueued && AbstractDungeon.actionManager.cardQueue.isEmpty()
					&& !AbstractDungeon.actionManager.hasControl) {
				endTurn();
				endTurnQueued = false;
			}
	    }
	    
		private static void endTurn() {
			AbstractPlayer p = INSTANCE.p();
			p.applyEndOfTurnTriggers();

			addToBot(new ClearCardQueueAction());
			addToBot(new DiscardAtEndOfTurnAction());
			
			Stream.of(p.drawPile, p.discardPile, p.hand).flatMap(g -> g.group.stream())
					.forEach(c -> c.resetAttributes());
			if (p.hoveredCard != null)
				p.hoveredCard.resetAttributes();

			INSTANCE.addTmpActionToBot(() -> {
				addToBot(new EndTurnAction());
				addToBot(new WaitAction(Settings.FAST_MODE ? 0.1F : 1.2F));
				startMonsterTurn = true;
			});
		}
	    
	    private static void updateMonsterTurn() {
	    	if (!AbstractDungeon.actionManager.actions.isEmpty())
	        	return;
	    	if (!startMonsterTurn)
	    		return;
	    	startMonsterTurn = false;
			for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
				if ((!m.isDying) && (!m.isEscaping)) {
					if (!m.hasPower("Barricade"))
						m.loseBlock();
					m.applyStartOfTurnPowers();
				}
			}
			startNextTurn = true;
	    }
	    
	    @SuppressWarnings("deprecation")
		private static void updateStartNextTurn() {
	    	GameActionManager gam = AbstractDungeon.actionManager;
	    	if (!gam.actions.isEmpty())
	        	return;
	    	if (!startNextTurn)
	    		return;
			if ((gam.turnHasEnded) && (!AbstractDungeon.getMonsters().areMonstersBasicallyDead())) {
				startNextTurn = false;
				AbstractDungeon.getCurrRoom().monsters.applyEndOfTurnPowers();
				INSTANCE.p().cardsPlayedThisTurn = 0;
				gam.orbsChanneledThisTurn.clear();
				if (ModHelper.isModEnabled("Careless"))
					Careless.modAction();
				if (ModHelper.isModEnabled("ControlledChaos")) {
					ControlledChaos.modAction();
					INSTANCE.p().hand.applyPowers();
				}
				INSTANCE.p().applyStartOfTurnRelics();
				INSTANCE.p().applyStartOfTurnPreDrawCards();
				INSTANCE.p().applyStartOfTurnCards();
				INSTANCE.p().applyStartOfTurnPowers();
				INSTANCE.p().applyStartOfTurnOrbs();
				GameActionManager.turn++;
				gam.turnHasEnded = false;
				GameActionManager.totalDiscardedThisTurn = 0;
				gam.cardsPlayedThisTurn.clear();
				GameActionManager.damageReceivedThisTurn = 0;
				if ((!INSTANCE.p().hasPower("Barricade")) && (!INSTANCE.p().hasPower("Blur"))) {
					if (!INSTANCE.p().hasRelic("Calipers")) {
						INSTANCE.p().loseBlock();
					} else {
						INSTANCE.p().loseBlock(15);
					}
				}
				if (!AbstractDungeon.getCurrRoom().isBattleOver) {
					addToBot(new DrawCardAction(null, INSTANCE.p().gameHandSize, true));
					INSTANCE.p().applyStartOfTurnPostDrawRelics();
					INSTANCE.p().applyStartOfTurnPostDrawPowers();
					addToBot(new EnableEndTurnButtonAction());
				}
			}
	    }
	}
	
	static class RNGTools {
		public static Random copyRNG(Random source) {
			Random rng = new Random();
			rng.random = new RandomXS128(source.random.getState(0), source.random.getState(1));
			rng.counter = 0;
			return rng;
		}
	}
	
	public default Random copyRNG(Random source) {
		return RNGTools.copyRNG(source);
	}
	
	public default void playAgain(AbstractCard card, AbstractMonster m) {
		AbstractCard tmp = card.makeSameInstanceOf();
		p().limbo.addToBottom(tmp);
		tmp.current_x = card.current_x;
		tmp.current_y = card.current_y;
		tmp.target_x = (Settings.WIDTH / 2.0F - 300.0F * Settings.scale);
		tmp.target_y = (Settings.HEIGHT / 2.0F);
		if (m != null)
			tmp.calculateCardDamage(m);
		tmp.purgeOnUse = true;
		AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
	}
	
	public default void autoplayInOrder(AbstractCard self, ArrayList<AbstractCard> list, AbstractMonster m) {
		int i = 0;
		ArrayList<CardQueueItem> queue = AbstractDungeon.actionManager.cardQueue;
		int startIndex = AutoplayProgressManager.indexOfLastItemIn(queue) + 1;
		ArrayList<AbstractCard> history = AbstractDungeon.actionManager.cardsPlayedThisCombat;
		if (history.get(history.size() - 1) == self)
			startIndex = 0;
		
		for (AbstractCard card : list) {
			AbstractCard tmp = card.makeSameInstanceOf();
			p().limbo.addToBottom(tmp);
			tmp.current_x = card.current_x;
			tmp.current_y = card.current_y;
			tmp.target_x = (Settings.WIDTH / 2.0F - 300.0F * Settings.scale);
			tmp.target_y = (Settings.HEIGHT / 2.0F);
			if (m != null) {
				tmp.calculateCardDamage(m);
			}
			tmp.purgeOnUse = true;
			CardQueueItem item = new CardQueueItem(tmp, m, card.energyOnUse, true, true);
			if (!queue.isEmpty()) {
				queue.add(i + startIndex, item);
			} else {
				queue.add(item);
			}
			AutoplayProgressManager.saveLastQueueItem(item);
			i++;
		}
	}
	
	static class AutoplayProgressManager {
		private static CardQueueItem lastCardQueued = null;
		
		private static int indexOfLastItemIn(ArrayList<CardQueueItem> list) {
			if (lastCardQueued == null)
				return -1;
			return list.indexOf(lastCardQueued);
		}
		
		private static void saveLastQueueItem(CardQueueItem q) {
			lastCardQueued = q;
		}
	}
	
	public default void rollIntentAction(AbstractMonster m) {
		ArrayList<AbstractGameAction> actions = new ArrayList<AbstractGameAction>();
		actions.addAll(AbstractDungeon.actionManager.actions);
		m.takeTurn();
    	AbstractDungeon.actionManager.actions.clear();
    	AbstractDungeon.actionManager.actions.addAll(actions);
    	this.addTmpActionToTop(AbstractDungeon.getMonsters()::showIntent);
    	att(new RollMoveAction(m));
	}
	
	public static class ColorRegister {
		Color c;
		AbstractRelic r;
		public ColorRegister(Color c) {
			this.c = c;
		}
		public ColorRegister(Color c, AbstractRelic r) {
			this(c);
			this.r = r;
		}
		public void addToGlowChangerList(AbstractCard c) {
			CardGlowChanger.addCardToList(c, this.c);
			if (this.r != null)
				this.r.beginLongPulse();
		}
		public void removeFromGlowList(AbstractCard c) {
			CardGlowChanger.removeFromList(c, this.c);
		}
	}
	
	static class CardGlowChanger {
		private static final ArrayList<Color> COLOR_LIST = new ArrayList<Color>();
		private static final HashMap<AbstractCard, ArrayList<Color>> GLOW_COLORS =
				new HashMap<AbstractCard, ArrayList<Color>>();
		private static final ArrayList<AbstractCard> UPDATE_LIST = new ArrayList<AbstractCard>();
		private static final ArrayList<Integer> USED_COLOR = new ArrayList<Integer>();
		private static final ArrayList<AbstractCard> HARD_GLOW_LOCK = new ArrayList<AbstractCard>();

		private static void initialize() {
			Stream.of(Color.GOLD, new Color(0.0F, 1.0F, 0.0F, 0.25F), Color.PINK, Color.SKY, Color.PURPLE,
					Color.RED, Color.BROWN).forEach(COLOR_LIST::add);
		}
		
		private static void addCardToList(AbstractCard c, Color color) {
			if (!UPDATE_LIST.contains(c)) {
				UPDATE_LIST.add(c);
			}
			if (GLOW_COLORS.containsKey(c)) {
				tryAddColor(c, color);
			} else {
				GLOW_COLORS.put(c, new ArrayList<Color>());
				if (c.glowColor != null)
					tryAddColor(c, c.glowColor);
				else
					tryAddColor(c, new Color(0.2F, 0.9F, 1.0F, 0.25F));
				tryAddColor(c, color);
			}
		}
		
		private static void removeFromList(AbstractCard c, Color color) {
			if (!GLOW_COLORS.containsKey(c)) {
				if (!UPDATE_LIST.contains(c))
					return;
				UPDATE_LIST.remove(c);
				if (HARD_GLOW_LOCK.contains(c))
					HARD_GLOW_LOCK.remove(c);
			} else {
				ArrayList<Color> tmp = GLOW_COLORS.get(c);
				if (tmp.size() == 2) {
					c.glowColor = tmp.get(0).cpy();
					GLOW_COLORS.remove(c);
					UPDATE_LIST.remove(c);
					if (HARD_GLOW_LOCK.contains(c))
						HARD_GLOW_LOCK.remove(c);
					return;
				} else {
					tmp.remove(color);
				}
			}
		}
		
		private static void addHardLockGlow(AbstractCard c) {
			if (!HARD_GLOW_LOCK.contains(c))
				HARD_GLOW_LOCK.add(c);
		}
		
		private static void tryAddColor(AbstractCard c, Color color) {
			if (!GLOW_COLORS.get(c).contains(color))
				GLOW_COLORS.get(c).add(color);
		}
		
		private static void updateGlow() {
			for (AbstractCard c : UPDATE_LIST) {
				ArrayList<Color> list = GLOW_COLORS.get(c);
				for (int i = 0; i < list.size(); i++) {
					if (c.glowColor.equals(list.get(i))) {
						if (i + 1 == list.size())
							c.glowColor = list.get(0).cpy();
						else
							c.glowColor = list.get(i + 1).cpy();
						break;
					}
				}
			}
			HARD_GLOW_LOCK.forEach(c -> c.beginGlowing());
		}
		
		private static Color unusedGlowColor() {
			if (COLOR_LIST.isEmpty())
				initialize();
			if (USED_COLOR.size() >= COLOR_LIST.size())
				return Color.BLACK;
			for (int i = 0; i < USED_COLOR.size() + 1; i++) {
				if (USED_COLOR.contains(i))
					continue;
				USED_COLOR.add(i);
				return COLOR_LIST.get(i);
			}
			return Color.BLACK;
		}
	}
	
	public default boolean inCombat() {
		return AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT;
	}
	
	public default Color initGlowColor() {
		return CardGlowChanger.unusedGlowColor();
	}
	
	public default void updateGlow() {
		CardGlowChanger.updateGlow();
	}
	
	public default void addToGlowChangerList(AbstractCard c, Color color) {
		CardGlowChanger.addCardToList(c, color);
	}
	
	public default void removeFromGlowList(AbstractCard c, Color color) {
		CardGlowChanger.removeFromList(c, color);
	}
	
	public default void addHardLockGlow(AbstractCard c) {
		CardGlowChanger.addHardLockGlow(c);
	}

	public default boolean hasEnemies() {
		return !(AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null
				|| AbstractDungeon.getMonsters() == null || AbstractDungeon.getMonsters().monsters == null);
	}
	
	public default double gainGold(double amount) {
		return amount;
	}
	
	public default ArrayList<String> relicPool(RelicTier t) {
		switch (t) {
		case BOSS:
			return AbstractDungeon.bossRelicPool;
		case COMMON:
			return AbstractDungeon.commonRelicPool;
		case RARE:
			return AbstractDungeon.rareRelicPool;
		case SHOP:
			return AbstractDungeon.shopRelicPool;
		case UNCOMMON:
			return AbstractDungeon.uncommonRelicPool;
		case STARTER:
		case SPECIAL:
		case DEPRECATED:
		default:
			return null;
		}
	}
	
	public static String energy(int amount) {
		return INSTANCE.energyString(amount);
	}
	
	public default String energyString(int amount) {
		if (amount < 4 && amount > 0) {
			return this.getIdenticalList("[E] ", amount).stream().reduce(" ", (a, b) -> a + b);
		} else {
			return amount + " [E] ";
		}
	}
	
	public default <T> void streamIfElse(Stream<T> s, Predicate<? super T> p, Consumer<? super T> c1,
			Consumer<? super T> c2) {
		s.forEach(ifElse(p, c1, c2));
	}

	public default <T> Consumer<T> ifElse(Predicate<? super T> p, Consumer<? super T> c1, Consumer<? super T> c2) {
		return t -> ((Consumer<T>) (p.test(t) ? c1 : c2)).accept(t);
	}
	
	static Function andThen(Function f, Function g) {
		return f.andThen(g);
	}
	
	public default <T, R> void streamMaps(Stream<T> s, Consumer<R> action, Function... functions) {
		s.forEach(functionsConsumer(action, functions));
	}
	
	public default <T, R> Consumer<T> functionsConsumer(Consumer<R> action, Function... functions) {
		Function<T, R> f = (Function<T, R>) Stream.of(functions).reduce(t -> t, (u, v) -> andThen(u, v));
		return t -> action.accept(f.apply(t));
	}
	
	public default <T, R> Consumer<T> f(Consumer<R> action, Function<T, R> f) {
		return t -> action.accept(f.apply(t));
	}
	
	public default <T, R, V> Function<Pair<T, R>, V> merge(BiFunction<T, R, V> f) {
		return p -> f.apply(p.getKey(), p.getValue());
	}
	
	public default <T, R> Consumer<Pair<T, R>> consumer(BiConsumer<T, R> action) {
		return p -> action.accept(p.getKey(), p.getValue());
	}

	public default <T, R, V> Function<T, Pair<R, V>> split(Function<T, R> f, Function<T, V> g) {
		return t -> split(t, f, g);
	}
	
	public default <T, R, V> Pair<R, V> split(T t, Function<T, R> f, Function<T, V> g) {
		return new Pair<R, V>(f.apply(t), g.apply(t));
	}
	
	public default <T> Consumer<T> combine(Consumer<T>... actions) {
		return Stream.of(actions).reduce(t -> {}, Consumer::andThen);
	}
	
	public static interface Lambda extends Runnable {}
	
	public default void addTmpActionToTop(Lambda... lambda) {
		att(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				Stream.of(lambda).forEach(Lambda::run);
			}
		});
	}
	
	public default void addTmpActionToBot(Lambda... lambda) {
		atb(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				Stream.of(lambda).forEach(Lambda::run);
			}
		});
	}
	
	public default void addTmpActionToTop(AbstractGameAction... actions) {
		att(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				ArrayList<AbstractGameAction> list = new ArrayList<AbstractGameAction>();
				Stream.of(actions).forEach(a -> list.add(0, a));
				list.forEach(INSTANCE::att);
			}
		});
	}
	
	public default void addTmpActionToBot(AbstractGameAction... actions) {
		atb(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				ArrayList<AbstractGameAction> list = new ArrayList<AbstractGameAction>();
				Stream.of(actions).forEach(a -> list.add(0, a));
				list.forEach(INSTANCE::att);
			}
		});
	}

	public default void addTmpXCostActionToTop(AbstractCard c, Consumer<Integer> action) {
		att(new AbstractXCostAction(c, action) {});
	}
	
	public default void addTmpXCostActionToBot(AbstractCard c, Consumer<Integer> action) {
		atb(new AbstractXCostAction(c, action) {});
	}
	
	public static void addToTop(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToTop(a);
	}
	
	public static void addToBot(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToBottom(a);
	}
	
	public default void att(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToTop(a);
	}
	
	public default void atb(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToBottom(a);
	}
	
	public default <T> Consumer<T> empty() {
		return t -> {};
	}
	
    public default <T> Predicate<T> not(Predicate<T> a) {
    	return a.negate();
    }

	public default <T> Predicate<T> and(Predicate<T>... list) {
    	return Stream.of(list).reduce(a -> true, Predicate::and);
    }

	public default <T> Predicate<T> or(Predicate<T>... list) {
    	return Stream.of(list).reduce(a -> false, Predicate::or);
    }
	
	public default <T> UnaryOperator<T> get(UnaryOperator<T> f) {
		return f;
	}
	
	public default <T> UnaryOperator<T> t() {
		return t -> t;
	}
	
	public default <T> UnaryOperator<T> chain(UnaryOperator<T> a, UnaryOperator<T> b) {
		return c -> b.apply(a.apply(c));
	}
	
	public default <T> UnaryOperator<T> chain(Stream<UnaryOperator<T>> s) {
		return s.reduce(t(), this::chain);
	}
	
	public default <T> Collector<T, ?, ArrayList<T>> toArrayList() {
		return Collectors.toCollection(ArrayList::new);
	}
	
	public default <T> ArrayList<T> createList(T... elements) {
		return Stream.of(elements).collect(this.toArrayList());
	}
	
	public default Stream<AbstractRelic> replicaRelicStream() {
		ArrayList<AbstractRelic> list = new ArrayList<AbstractRelic>();
		list.addAll(p().relics);
		return list.stream();
	}
	
	public default Stream<AbstractTestRelic> relicStream() {
		return TestMod.MY_RELICS.stream().flatMap(r -> this.relicStream(r.getClass()));
	}
	
	public default <T extends AbstractTestRelic> Stream<T> relicStream(Class<T> sample) {
		return p().relics.stream().filter(r -> r.getClass().isAssignableFrom(sample)).map(r -> (T) r)
				.collect(toArrayList()).stream();
	}
	
	public default <T> T last(ArrayList<T> list) {
		if (list == null || list.isEmpty())
			return null;
		return list.get(list.size() - 1);
	}
	
	public default ApplyPowerAction apply(AbstractCreature source, AbstractPower p) {
		return p.amount == 0 || (p.amount == -1 && !p.canGoNegative) ? new ApplyPowerAction(p.owner, source, p)
				: new ApplyPowerAction(p.owner, source, p, p.amount);
	}
	
	public default void regainPowerOnRemove(AbstractTestPower p, UnaryOperator<AbstractTestPower> f, boolean noStack,
			boolean top) {
		this.addTmpActionToTop(() -> {
			AbstractTestPower po = f.apply(p);
			Consumer<ArrayList<AbstractPower>> c = top ? (l) -> l.add(0, po) : (l) -> l.add(po);
			if (!(noStack && po.owner.hasPower(po.ID))) {
				c.accept(po.owner.powers);
			}
		});
	}
	
	public default void stupidDevToBot(AbstractGameAction a) {
		this.atb(new SmhStupidDev(a));
	}
	
	public default void stupidDevToBot(Lambda l) {
		this.atb(new SmhStupidDev(l));
	}
	
	public default void stupidDevToTop(AbstractGameAction a) {
		this.att(new SmhStupidDev(a));
	}
	
	public default void stupidDevToTop(Lambda l) {
		this.att(new SmhStupidDev(l));
	}
	
	static class SmhStupidDev extends AbstractGameAction {
		AbstractGameAction a;
		Lambda l;
		public SmhStupidDev(AbstractGameAction a) {
			this.a = a;
		}
		public SmhStupidDev(Lambda l) {
			this.l = l;
		}
		@Override
		public void update() {
			if (this.a != null) {
				this.a.update();
				this.isDone = this.a.isDone;
			} else if (this.l != null) {
				this.l.run();
				this.isDone = true;
			}
		}
	}
	
	@SpirePatch(clz = GameActionManager.class, method = "clearPostCombatActions")
	public static class GameActionManagerClearPostCombatActionsPatch {
		public static ExprEditor Instrument() {
			return new ExprEditor() {
				public void edit(Instanceof i) throws CannotCompileException {
					try {
						if (i.getType().getName().equals(UseCardAction.class.getName())) {
							i.replace("$_ = $proceed($$) || e instanceof utils.MiscMethods.SmhStupidDev;");
						}
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				}
			};
		}
	}
	
}
