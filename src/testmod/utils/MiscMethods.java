package testmod.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.evacipated.cardcrawl.modthespire.Loader;
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
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;
import basemod.BaseMod;
import basemod.Pair;
import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;
import testmod.actions.AbstractXCostAction;
import testmod.mymod.TestMod;
import testmod.powers.AbstractTestPower;
import testmod.relics.AbstractTestRelic;
import testmod.relics.Prudence;
import testmod.relicsup.AbstractUpgradedRelic;
import testmod.relicsup.AllUpgradeRelic;

@SuppressWarnings({ "unchecked", "rawtypes" })
public interface MiscMethods {
	public static final MiscMethods MISC = new MiscMethods() {};
	
	default void print(Object s) {
		TestMod.info(s);
	}
	
	static String getIDForPowerWithoutLog(Class<?> c) {
		return GetID.getIDPrivate(c, "POWER_ID");
	}
	
	static class GetID {
		private static String getIDPrivate(Class<?> c, String varName) {
			try {
				c.getDeclaredField(varName);
			} catch (NoSuchFieldException | SecurityException e) {
				return c.getSimpleName();
			}
			return ReflectionHacks.getPrivateStatic(c, varName);
		}
	}
	
	static String getIDWithoutLog(Class<?> c) {
		return GetID.getIDPrivate(c, "ID");
	}

	static String getIDForUI() {
		return MISC.bottomClassExcept().getSimpleName();
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
	
	default int getDate() {
		return Calendar.getInstance().get(Calendar.DATE);
	}
	
	default AbstractPlayer p() {
		return AbstractDungeon.player;
	}
	
	default <T> ArrayList<T> getIdenticalList(T value, int size) {
		ArrayList<T> list = new ArrayList<T>();
		while (list.size() < size)
			list.add(value);
		return list;
	}
	
	default ArrayList<Integer> getNaturalNumberList(int n) {
		return getNumberList(0, n);
	}
	
	default ArrayList<Integer> getNumberList(int start, int end) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		int s = end > start ? 1 : -1;
		for (int i = start; (end - i) * s > 0; i += s)
			list.add(i);
		return list;
	}
	
	default void togglePulse(AbstractRelic r, boolean pulse) {
		Lambda f = pulse ? r::beginLongPulse : r::stopPulse;
		f.run();
	}
	
	default void markAsSeen(AbstractRelic r) {
		if (!UnlockTracker.isRelicSeen(r.relicId)) {
			UnlockTracker.markRelicAsSeen(r.relicId);
			TestMod.info("成功解锁了未见过的 " + r.name);
		}
		r.isSeen = true;
	}
	
	default void markAsSeen(AbstractCard c) {
		if (!UnlockTracker.isCardSeen(c.cardID)) {
			UnlockTracker.markCardAsSeen(c.cardID);
			TestMod.info("成功解锁了未见过的 " + c.name);
		}
		c.isSeen = true;
	}
	
	default CardGroup getSource(AbstractCard c) {
		return Stream.of(p().discardPile, p().hand, p().drawPile).filter(g -> g.contains(c)).findAny().orElse(null);
	}
	
	default boolean handFull() {
		return p().hand.size() >= BaseMod.MAX_HAND_SIZE;
	}
	
	default boolean isLocalTesting() {
		return TestMod.isLocalTest();
	}
	
	default void setXCostEnergy(AbstractCard c) {
		if (c.cost == -1)
			c.energyOnUse = EnergyPanel.totalCount;
	}
	
	default boolean hasPrudence() {
		return p().relics.stream().anyMatch(r -> r instanceof Prudence);
	}
	
	default void addHoarderCard(CardGroup g, AbstractCard c) {
		if (ModHelper.isModEnabled("Hoarder")) {
			addExtraCard(g, c, 2);
		}
	}
	
	default void addExtraCard(CardGroup g, AbstractCard c, int num) {
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
	
	default ArrayList<AbstractCard> getAllInBattleInstance(String cardID) {
		IdChecker checker = new IdChecker(cardID);
		return Stream.concat(combatCards(true, true), Stream.of(p().cardInUse)).filter(checker::check)
				.collect(toArrayList());
	}
	
	default void turnSkipperStart() {
    	TurnSkipper.start();
    } 
	
	default void turnSkipperStartByCard(AbstractCard c) {
		TurnSkipper.startByCard(c);
    }
	
	default void turnSkipperUpdate() {
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
	    	MISC.addTmpActionToBot(() -> {
				AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
				AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
				startEndingTurn = true;
			});
		}
	    
	    public static void startByCard(AbstractCard c) {
	    	if (inProgress())
	    		return;
			start();
			MISC.addTmpActionToBot(() -> {
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
	    	
	    	MISC.p().limbo.group.forEach(c -> AbstractDungeon.effectList.add(new ExhaustCardEffect(c)));
	    	
	    	TestMod.info("limbo数量 = " + MISC.p().limbo.size());
	    	MISC.p().limbo.group.clear();
	    	MISC.p().releaseCard();
	        // Start Ending Turn
	        
	        addToBot(new NewQueueCardAction());
	        EndTurnButton etb = AbstractDungeon.overlayMenu.endTurnButton;
	        etb.enabled = false;
	        etb.isGlowing = false;
	        etb.updateText(EndTurnButton.ENEMY_TURN_MSG);
	        CardCrawlGame.sound.play("END_TURN");
	        MISC.p().releaseCard();
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
			AbstractPlayer p = MISC.p();
			p.applyEndOfTurnTriggers();

			addToBot(new ClearCardQueueAction());
			addToBot(new DiscardAtEndOfTurnAction());
			
			Stream.of(p.drawPile, p.discardPile, p.hand).flatMap(g -> g.group.stream())
					.forEach(c -> c.resetAttributes());
			if (p.hoveredCard != null)
				p.hoveredCard.resetAttributes();

			MISC.addTmpActionToBot(() -> {
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
				MISC.p().cardsPlayedThisTurn = 0;
				gam.orbsChanneledThisTurn.clear();
				if (ModHelper.isModEnabled("Careless"))
					Careless.modAction();
				if (ModHelper.isModEnabled("ControlledChaos")) {
					ControlledChaos.modAction();
					MISC.p().hand.applyPowers();
				}
				MISC.p().applyStartOfTurnRelics();
				MISC.p().applyStartOfTurnPreDrawCards();
				MISC.p().applyStartOfTurnCards();
				MISC.p().applyStartOfTurnPowers();
				MISC.p().applyStartOfTurnOrbs();
				GameActionManager.turn++;
				gam.turnHasEnded = false;
				GameActionManager.totalDiscardedThisTurn = 0;
				gam.cardsPlayedThisTurn.clear();
				GameActionManager.damageReceivedThisTurn = 0;
				if ((!MISC.p().hasPower("Barricade")) && (!MISC.p().hasPower("Blur"))) {
					if (!MISC.p().hasRelic("Calipers")) {
						MISC.p().loseBlock();
					} else {
						MISC.p().loseBlock(15);
					}
				}
				if (!AbstractDungeon.getCurrRoom().isBattleOver) {
					addToBot(new DrawCardAction(null, MISC.p().gameHandSize, true));
					MISC.p().applyStartOfTurnPostDrawRelics();
					MISC.p().applyStartOfTurnPostDrawPowers();
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
	
	default Random copyRNG(Random source) {
		return RNGTools.copyRNG(source);
	}
	
	default void playAgain(AbstractCard card, AbstractMonster m) {
		AbstractCard c = card.makeSameInstanceOf();
		c.purgeOnUse = true;
		if (m != null)
			c.calculateCardDamage(m);
		else
			c.applyPowers();
        att(new NewQueueCardAction(c, m, true, true));
        this.print("添加队列了:" + c.name);
	}
	
	default void autoplayInOrder(AbstractCard self, ArrayList<AbstractCard> list, AbstractMonster m) {
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
	
	default void rollIntentAction(AbstractMonster m) {
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
		Predicate<AbstractCard> p;
		public ColorRegister(Color c) {
			this.c = c;
		}
		public ColorRegister addRelic(AbstractRelic r) {
			this.r = r;
			return this;
		}
		public ColorRegister addPredicate(Predicate<AbstractCard> p) {
			this.p = p;
			return this;
		}
		public void addToGlowChangerList(AbstractCard c) {
			CardGlowChanger.addCardToList(c, this.c);
			if (this.r != null)
				this.r.beginLongPulse();
		}
		public void removeFromGlowList(AbstractCard c) {
			CardGlowChanger.removeFromList(c, this.c);
		}
		public void updateCard(AbstractCard c) {
			Consumer<AbstractCard> a = p != null && p.test(c) ? this::addToGlowChangerList : this::removeFromGlowList;
			a.accept(c);
		}
		public void updateHand() {
			MISC.p().hand.group.forEach(this::updateCard);
		}
	}
	
	default ColorRegister colorRegister(Color c) {
		return new ColorRegister(c);
	}
	
	static class CardGlowChanger {
		private static final ArrayList<Color> COLOR_LIST = new ArrayList<Color>();
		private static final HashMap<AbstractCard, ArrayList<Color>> GLOW_COLORS =
				new HashMap<AbstractCard, ArrayList<Color>>();
		private static final ArrayList<AbstractCard> UPDATE_LIST = new ArrayList<AbstractCard>();
		private static final ArrayList<Integer> USED_COLOR = new ArrayList<Integer>();
		private static final ArrayList<AbstractCard> HARD_GLOW_LOCK = new ArrayList<AbstractCard>();
		private static final int DURATION = 10;
		private static int phase = 0;

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
				if (tmp.size() == 2 && tmp.get(1).equals(color)) {
					c.glowColor = tmp.get(0).cpy();
					tmp.clear();
					GLOW_COLORS.remove(c);
					UPDATE_LIST.remove(c);
					if (HARD_GLOW_LOCK.contains(c))
						HARD_GLOW_LOCK.remove(c);
				} else if (tmp.size() > 2) {
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
			UPDATE_LIST.stream().filter(c -> !MISC.p().hand.group.contains(c)).forEach(GLOW_COLORS::remove);
			UPDATE_LIST.removeIf(c -> !MISC.p().hand.group.contains(c));
			HARD_GLOW_LOCK.removeIf(c -> !MISC.p().hand.group.contains(c));
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
	
	default boolean inCombat() {
		return AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT;
	}
	
	default Color initGlowColor() {
		return CardGlowChanger.unusedGlowColor();
	}
	
	default void updateGlow() {
		if (CardGlowChanger.phase++ == CardGlowChanger.DURATION) {
			CardGlowChanger.phase = 0;
			CardGlowChanger.updateGlow();
		}
	}
	
	default void resetGlow() {
		CardGlowChanger.UPDATE_LIST.clear();
		CardGlowChanger.HARD_GLOW_LOCK.clear();
		CardGlowChanger.GLOW_COLORS.values().forEach(l -> l.clear());
		CardGlowChanger.GLOW_COLORS.clear();
	}
	
	default void addToGlowChangerList(AbstractCard c, Color color) {
		CardGlowChanger.addCardToList(c, color);
	}
	
	default void removeFromGlowList(AbstractCard c, Color color) {
		CardGlowChanger.removeFromList(c, color);
	}
	
	default void addHardLockGlow(AbstractCard c) {
		CardGlowChanger.addHardLockGlow(c);
	}

	default boolean hasEnemies() {
		return !(AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null
				|| AbstractDungeon.getMonsters() == null || AbstractDungeon.getMonsters().monsters == null);
	}
	
	default double gainGold(double amount) {
		return amount;
	}
	
	default ArrayList<String> relicPool(RelicTier t) {
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
		return MISC.energyString(amount);
	}
	
	default String energyString(int amount) {
		if (amount < 4 && amount > 0) {
			return this.getIdenticalList("[E] ", amount).stream().reduce(" ", (a, b) -> a + b);
		} else {
			return amount + " [E] ";
		}
	}
	
	default <T> void streamIfElse(Stream<T> s, Predicate<? super T> p, Consumer<? super T> c1,
			Consumer<? super T> c2) {
		s.forEach(ifElse(p, c1, c2));
	}

	default <T> Consumer<T> ifElse(Predicate<? super T> p, Consumer<? super T> c1, Consumer<? super T> c2) {
		return t -> ((Consumer<T>) (p.test(t) ? c1 : c2)).accept(t);
	}
	
	default <T> Function<? super T, ? extends Stream<? extends T>> flatmapIfElse(Predicate<? super T> p,
			Consumer<? super T> c1, Consumer<? super T> c2) {
		return t -> {
			ifElse(p, c1, c2);
			boolean flag = p.test(t);
			return flag ? Stream.of(t) : Stream.empty();
		};
	}
	
	static Function andThen(Function f, Function g) {
		return f.andThen(g);
	}
	
	default <T, R> void streamMaps(Stream<T> s, Consumer<R> action, Function... functions) {
		s.forEach(functionsConsumer(action, functions));
	}
	
	default <T, R> Consumer<T> functionsConsumer(Consumer<R> action, Function... functions) {
		Function<T, R> f = (Function<T, R>) Stream.of(functions).reduce(t -> t, (u, v) -> andThen(u, v));
		return t -> action.accept(f.apply(t));
	}
	
	default <T, R> Consumer<T> f(Consumer<R> action, Function<T, R> f) {
		return t -> action.accept(f.apply(t));
	}
	
	default <T, R, V> Function<Pair<T, R>, V> merge(BiFunction<T, R, V> f) {
		return p -> f.apply(p.getKey(), p.getValue());
	}
	
	default <T, R> Consumer<Pair<T, R>> consumer(BiConsumer<T, R> action) {
		return p -> action.accept(p.getKey(), p.getValue());
	}

	default <T, R, V> Function<T, Pair<R, V>> split(Function<T, R> f, Function<T, V> g) {
		return t -> split(t, f, g);
	}
	
	default <T, R, V> Pair<R, V> split(T t, Function<T, R> f, Function<T, V> g) {
		return new Pair<R, V>(f.apply(t), g.apply(t));
	}
	
	default <T> Consumer<T> combine(Consumer<T>... actions) {
		return Stream.of(actions).reduce(t -> {}, Consumer::andThen);
	}
	
	static class TMPEffect extends AbstractGameEffect {
		private Lambda[] f;
		public TMPEffect(Lambda... f) {
			this.f = f;
			this.color = Color.BLACK.cpy();
		}
		public void update() {
			super.update();
			this.isDone = true;
			Stream.of(this.f).forEach(Lambda::run);
		}
		@Override
		public void dispose() {
		}
		@Override
		public void render(SpriteBatch arg0) {
		}
	}

	default void addTmpEffect(Lambda... lambda) {
		AbstractDungeon.topLevelEffectsQueue.add(new TMPEffect(lambda));
	}
	
	default void addLowLevelEffect(Lambda... lambda) {
		AbstractDungeon.effectsQueue.add(new TMPEffect(lambda));
	}
	
	public static interface Lambda extends Runnable {}
	
	default void addTmpActionToTop(Lambda... lambda) {
		att(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				Stream.of(lambda).forEach(Lambda::run);
			}
		});
	}
	
	default void addTmpActionToBot(Lambda... lambda) {
		atb(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				Stream.of(lambda).forEach(Lambda::run);
			}
		});
	}
	
	default void addTmpActionToTop(AbstractGameAction... actions) {
		att(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				ArrayList<AbstractGameAction> list = new ArrayList<AbstractGameAction>();
				Stream.of(actions).forEach(a -> list.add(0, a));
				list.forEach(MISC::att);
			}
		});
	}
	
	default void addTmpActionToBot(AbstractGameAction... actions) {
		atb(new AbstractGameAction() {
			@Override
			public void update() {
				this.isDone = true;
				ArrayList<AbstractGameAction> list = new ArrayList<AbstractGameAction>();
				Stream.of(actions).forEach(a -> list.add(0, a));
				list.forEach(MISC::att);
			}
		});
	}

	default void addTmpXCostActionToTop(AbstractCard c, Consumer<Integer> action) {
		att(new AbstractXCostAction(c, action) {});
	}
	
	default void addTmpXCostActionToBot(AbstractCard c, Consumer<Integer> action) {
		atb(new AbstractXCostAction(c, action) {});
	}
	
	public static void addToTop(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToTop(a);
	}
	
	public static void addToBot(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToBottom(a);
	}
	
	default void att(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToTop(a);
	}
	
	default void atb(AbstractGameAction a) {
		AbstractDungeon.actionManager.addToBottom(a);
	}
	
	default <T> Consumer<T> empty() {
		return t -> {};
	}
	
    default <T> Predicate<T> not(Predicate<T> a) {
    	return a.negate();
    }

	default <T> Predicate<T> and(Predicate<T>... list) {
    	return Stream.of(list).reduce(a -> true, Predicate::and);
    }

	default <T> Predicate<T> or(Predicate<T>... list) {
    	return Stream.of(list).reduce(a -> false, Predicate::or);
    }
	
	default <T> Supplier<T> get(Supplier<T> f) {
		return f;
	}
	
	default <T> UnaryOperator<T> get(UnaryOperator<T> f) {
		return f;
	}
	
	default <T> UnaryOperator<T> t() {
		return t -> t;
	}
	
	default <T> UnaryOperator<T> chain(UnaryOperator<T> a, UnaryOperator<T> b) {
		return c -> b.apply(a.apply(c));
	}
	
	default <T> UnaryOperator<T> chain(Stream<UnaryOperator<T>> s) {
		return s.reduce(t(), this::chain);
	}
	
	default <T> Collector<T, ?, ArrayList<T>> toArrayList() {
		return Collectors.toCollection(ArrayList::new);
	}
	
	default <T> ArrayList<T> createList(T... elements) {
		return Stream.of(elements).collect(this.toArrayList());
	}
	
	default Stream<AbstractRelic> replicaRelicStream() {
		ArrayList<AbstractRelic> list = new ArrayList<AbstractRelic>();
		list.addAll(p().relics);
		return list.stream();
	}
	
	default Stream<AbstractTestRelic> relicStream() {
		Stream.Builder<AbstractTestRelic> b = Stream.builder();
		p().relics.stream().filter(r -> r instanceof AbstractTestRelic).map(r -> (AbstractTestRelic) r).forEach(b::add);
		return b.build();
	}
	
	default <T extends AbstractTestRelic> Stream<T> relicStream(Class<T> sample) {
		return p().relics.stream().filter(r -> r.getClass().isAssignableFrom(sample)).map(r -> (T) r)
				.collect(toArrayList()).stream();
	}
	
	default <T> T last(T first, T second) {
		return second;
	}
	
	default ApplyPowerAction apply(AbstractCreature source, AbstractPower p) {
		return new ApplyPowerAction(p.owner, source, p);
	}
	
	default void regainPowerOnRemove(AbstractTestPower p, UnaryOperator<AbstractTestPower> f, boolean noStack,
			boolean top) {
		this.addTmpActionToTop(() -> {
			AbstractTestPower po = f.apply(p);
			Consumer<ArrayList<AbstractPower>> c = top ? (l) -> l.add(0, po) : (l) -> l.add(po);
			if (!(noStack && po.owner.hasPower(po.ID))) {
				c.accept(po.owner.powers);
			}
		});
	}
	
	default void stupidDevToBot(AbstractGameAction a) {
		this.atb(new SmhStupidDev(a));
	}
	
	default void stupidDevToBot(Lambda l) {
		this.atb(new SmhStupidDev(l));
	}
	
	default void stupidDevToTop(AbstractGameAction a) {
		this.att(new SmhStupidDev(a));
	}
	
	default void stupidDevToTop(Lambda l) {
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
							i.replace("$_ = $proceed($$) || e instanceof testmod.utils.MiscMethods.SmhStupidDev;");
						}
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				}
			};
		}
	}
	
	default Stream<StackTraceElement> stackTrace() {
		return Stream.of(Thread.currentThread().getStackTrace());
	}
	
	default boolean hasStack(String className, String methodName) {
		return stackTrace().anyMatch(e -> className.equals(e.getClassName()) && methodName.equals(e.getMethodName()));
	}
	
	default String bottomClassNameExcept(Class<?>... sample) {
		return stackTrace().map(i -> i.getClassName()).filter(s -> Stream.concat(Stream.of(MiscMethods.class,
				Thread.class), Stream.of(sample)).noneMatch(a -> s.equals(a.getCanonicalName()))).findFirst().get();
	}
	
	default <T> Class<T> bottomClassExcept(Class<?>... exception) {
		try {
			return (Class<T>) Class.forName(bottomClassNameExcept(exception));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	default <T, R extends T> Class<R> get(Class<? extends T>... exception) {
		return bottomClassExcept(exception);
	}
	
	default Stream<AbstractCard> combatCards() {
		return Stream.of(p().drawPile, p().hand, p().discardPile).flatMap(g -> g.group.stream());
	}
	
	default Stream<AbstractCard> combatCards(boolean limbo, boolean exhaust) {
		Stream<CardGroup> l = limbo ? Stream.of(p().limbo) : Stream.empty();
		Stream<CardGroup> e = exhaust ? Stream.of(p().exhaustPile) : Stream.empty();
		return Stream.concat(combatCards(), Stream.concat(l, e).flatMap(g -> g.group.stream()));
	}
	
	default void forEachCombatCard(boolean limbo, boolean exhaust, Consumer<AbstractCard> f) {
		combatCards(limbo, exhaust).forEach(f);
	}
	
	default int damageTimes(AbstractMonster m) {
		if (m.intent.ordinal() > 3)
			return 0;
		int amount = ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentMultiAmt");
		return amount > 1 ? amount : 1;
	}
	
	default <T> ArrayList<T> reverse(ArrayList<T> l) {
		ArrayList<T> tmp = new ArrayList<T>();
		l.forEach(a -> tmp.add(0, a));
		return tmp;
	}
	
	default <T> Stream<T> reverse(Stream<T> s) {
		return s.map(StreamReverser::flatReverse).reduce(Stream.empty(), StreamReverser::reverseReducer);
	}
	
	static class StreamReverser {
		public static <T> Stream<T> flatReverse(T t) {
			return Stream.of(t);
		}
		public static <T> Stream<T> reverseReducer(Stream<T> a, Stream<T> b) {
			return Stream.concat(b, a);
		}
	}
	
	static class RandomKey {
		private static final ArrayList<ObtainKeyEffect.KeyColor> COLOR = new ArrayList<ObtainKeyEffect.KeyColor>();
		
		public static void nextKey(Random rng) {
			if (COLOR.isEmpty())
				Stream.of(ObtainKeyEffect.KeyColor.values()).forEach(COLOR::add);
			Collections.shuffle(COLOR, new java.util.Random(rng.randomLong()));
			AbstractDungeon.topLevelEffectsQueue.add(new ObtainKeyEffect(COLOR.get(0)));
		}
	}
	
	default void addRandomKey(Random rng) {
		RandomKey.nextKey(rng);
	}
	
	default void addRandomKey() {
		RandomKey.nextKey(AbstractDungeon.miscRng);
	}
	
	default AbstractMonster randomMonster() {
		return AbstractDungeon.getMonsters().getRandomMonster(null, true, this.copyRNG(AbstractDungeon.monsterRng));
	}
	
	default boolean upgraded(AbstractRelic r) {
		if (Loader.isModLoaded("RelicUpgradeLib"))
			return AllUpgradeRelic.isUpgraded(r);
		return r instanceof AbstractUpgradedRelic;
	}
	
	default boolean canUpgrade(AbstractRelic r) {
		if (Loader.isModLoaded("RelicUpgradeLib"))
			return AllUpgradeRelic.upgradable(r);
		return r instanceof AbstractTestRelic && ((AbstractTestRelic) r).canUpgrade();
	}
	
	default AbstractRelic tryUpgrade(AbstractRelic r) {
		if (Loader.isModLoaded("RelicUpgradeLib") && AllUpgradeRelic.upgradable(r))
			return AllUpgradeRelic.getUpgrade(r).makeCopy();
		if (r instanceof AbstractTestRelic && ((AbstractTestRelic) r).canUpgrade())
			return ((AbstractTestRelic) r).upgrade();
		return r;
	}
	
	default void updateHandSize(int delta) {
		BaseMod.MAX_HAND_SIZE += delta;
		relicStream().filter(r -> r instanceof HandSizeCounterUpdater).forEach(r -> r.counter = BaseMod.MAX_HAND_SIZE);
	}
	
	public static class Prime {
    	private static final ArrayList<Integer> PRIME = new ArrayList<Integer>();

    	public static void clear() {
    		PRIME.clear();
    	}
    	
    	static int find(int n) {
    		if (n < 3)
    			return n + 1;
    		if (PRIME.isEmpty()) {
    			PRIME.add(2);
    			PRIME.add(3);
    		}
    		for (int x = PRIME.get(PRIME.size() - 1); PRIME.size() < n;)
    			addIfIsPrime(x += 2);
    		return PRIME.get(n - 1);
    	}

    	private static void addIfIsPrime(int x) {
    		for (int i = 1; PRIME.get(i) <= (int) Math.sqrt(x) && i < PRIME.size(); i++)
    			if (x % PRIME.get(i) == 0)
    				return;
    		PRIME.add(x);
    	}

    	private static int indexOf(int start, int num) {
    		int size = PRIME.size();
    		if (num < 2 || num > 2147483629)
    			return -1;
    		if (size > 0 && PRIME.get(size - 1) >= num) {
    			for (int i = start; i < size; i++)
    				if (PRIME.get(i) == num)
    					return i + 1;
    			return -1;
    		}
    		if (size > 100000000)
    			find(105097564);
    		else if (size > 10000000)
    			find(size + 200000);
    		else if (size > 1000000)
    			find(size + 15000);
    		else if (size > 100000)
    			find(size + 1000);
    		else
    			find(Math.max(size * 2, 10000));
    		return indexOf(size, num);
    	}

    	public static int indexOf(int num) {
    		return indexOf(0, num);
    	}

    	public static boolean isPrime(int num) {
    		if (num < 2)
    			return false;
    		if (PRIME.size() < 100)
    			find(100);
    		if (PRIME.contains(num))
    			return true;
    		for (int i = 0; i < PRIME.size() && PRIME.get(i) <= (int) Math.sqrt(num); i++)
    			if (num % PRIME.get(i) == 0)
    				return false;
    		int preSize = PRIME.size();
    		if (PRIME.get(PRIME.size() - 1) <= (int) Math.sqrt(num)) {
    			indexOf((int) Math.sqrt(num));
        		for (int i = preSize; PRIME.get(i) <= (int) Math.sqrt(num) && i < PRIME.size(); i++)
        			if (num % PRIME.get(i) == 0)
        				return false;
    		}
    		return true;
    	}
    	
    	public static ArrayList<Integer> primeFactorOf(int num) {
    		if (num < 2 || isPrime(num)) {
        		ArrayList<Integer> tmp = new ArrayList<Integer>();
    			tmp.add(num);
    			return tmp;
    		}
    		for (int i = 0, current = 2;; i++, current = PRIME.get(i))
    			if (num % current == 0)
    				return combine(current, primeFactorOf(num / current));
    	}
    	
    	private static ArrayList<Integer> combine(int a, ArrayList<Integer> b){
    		b.add(a);
    		return b;
    	}
    }
}