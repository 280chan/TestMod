package utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction;
import com.megacrit.cardcrawl.actions.common.EndTurnAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Careless;
import com.megacrit.cardcrawl.daily.mods.ControlledChaos;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;

import mymod.TestMod;
import relics.Prudence;
import relics.StringDisintegrator;

public interface MiscMethods {
	
	public default int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}
	
	public default int getDate() {
		return Calendar.getInstance().get(Calendar.DATE);
	}
	
	public default boolean isLocalTesting() {
		return TestMod.isLocalTest();
	}
	
	public default boolean hasPrudence() {
		return AbstractDungeon.player.hasRelic(TestMod.makeID(Prudence.ID));
	}
	
	public default boolean hasStringDisintegrator() {
		return AbstractDungeon.player.hasRelic(TestMod.makeID(StringDisintegrator.ID));
	}
	
	public default void addHoarderCard(CardGroup g, AbstractCard c) {
		if (ModHelper.isModEnabled("Hoarder")) {
			addExtraCard(g, c, 2);
		}
	}
	
	public default void addExtraCard(CardGroup g, AbstractCard c, int num) {
		for (int i = 0; i < num; i++)
			g.addToTop(c.makeStatEquivalentCopy());
	}
	
	public default ArrayList<AbstractCard> getAllInBattleInstance(String cardID) {
		ArrayList<AbstractCard> cards = new ArrayList<AbstractCard>();
		AbstractPlayer p = AbstractDungeon.player;
		if (p.cardInUse.cardID.equals(cardID))
			cards.add(p.cardInUse);
		for (AbstractCard c : p.drawPile.group)
			if (c.cardID.equals(cardID))
				cards.add(c);
		for (AbstractCard c : p.discardPile.group)
			if (c.cardID.equals(cardID))
				cards.add(c);
		for (AbstractCard c : p.exhaustPile.group)
			if (c.cardID.equals(cardID))
				cards.add(c);
		for (AbstractCard c : p.limbo.group)
			if (c.cardID.equals(cardID))
				cards.add(c);
		for (AbstractCard c : p.hand.group)
			if (c.cardID.equals(cardID))
				cards.add(c);
		TestMod.info("Count: " + cards.size());
		return cards;
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
	    	AbstractDungeon.actionManager.addToBottom(new AbstractGameAction(){
				@Override
				public void update() {
					this.isDone = true;
					AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
					AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
			    	startEndingTurn = true;
				}});
	    }
	    
	    public static void startByCard(AbstractCard c) {
	    	if (inProgress())
	    		return;
			start();
			AbstractDungeon.actionManager.addToBottom(new AbstractGameAction(){
				@Override
				public void update() {
					this.isDone = true;
					if (!c.dontTriggerOnUseCard)
						for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
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
				}});
	    }
	    
	    private static void startByCardTimeWarpIssues(AbstractPower p) {
	    	p.amount = -1;
			p.playApplyPowerSfx();
			CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
			AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
			AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
			for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
		        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 2), 2));
		    }
	    }
	    
	    private static void updateStartEndingTurn() {
	    	if (!startEndingTurn)
	    		return;
	    	startEndingTurn = false;
			for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
				if (i.autoplayCard) {
					i.card.dontTriggerOnUseCard = true;
					AbstractDungeon.actionManager.addToBottom(new UseCardAction(i.card));
				}
			}
	    	AbstractDungeon.actionManager.cardQueue.clear();
	    	for (AbstractCard c : AbstractDungeon.player.limbo.group) {
	            AbstractDungeon.effectList.add(new ExhaustCardEffect(c));
	        }
	    	TestMod.info("limbo数量 = " + AbstractDungeon.player.limbo.size());
	    	AbstractDungeon.player.limbo.group.clear();
	        AbstractDungeon.player.releaseCard();
	        // Start Ending Turn
	        
	        AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction());
	        EndTurnButton etb = AbstractDungeon.overlayMenu.endTurnButton;
	        etb.enabled = false;
	        etb.isGlowing = false;
	        etb.updateText(EndTurnButton.ENEMY_TURN_MSG);
	        CardCrawlGame.sound.play("END_TURN");
	        AbstractDungeon.player.releaseCard();
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
			AbstractDungeon.player.applyEndOfTurnTriggers();

			AbstractDungeon.actionManager.addToBottom(new ClearCardQueueAction());
			AbstractDungeon.actionManager.addToBottom(new DiscardAtEndOfTurnAction());
			for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
				c.resetAttributes();
			}
			for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
				c.resetAttributes();
			}
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				c.resetAttributes();
			}
			if (AbstractDungeon.player.hoveredCard != null) {
				AbstractDungeon.player.hoveredCard.resetAttributes();
			}

			AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
				public void update() {
					AbstractDungeon.actionManager.addToBottom(new EndTurnAction());
					if (Settings.FAST_MODE) {
						AbstractDungeon.actionManager.addToBottom(new WaitAction(0.1F));
					} else {
						AbstractDungeon.actionManager.addToBottom(new WaitAction(1.2F));
					}
					startMonsterTurn = true;
					this.isDone = true;
				}
			});
		}
	    
	    private static void updateMonsterTurn() {
	    	if (!AbstractDungeon.actionManager.actions.isEmpty())
	        	return;
	    	if (!startMonsterTurn)
	    		return;
	    	startMonsterTurn = false;
			MonsterGroup group = AbstractDungeon.getCurrRoom().monsters;
			for (AbstractMonster m : group.monsters) {
				if ((!m.isDying) && (!m.isEscaping)) {
					if (!m.hasPower("Barricade")) {
						m.loseBlock();
					}
					m.applyStartOfTurnPowers();
				}
			}
			startNextTurn = true;
	    }
	    
	    private static void updateStartNextTurn() {
	    	GameActionManager gam = AbstractDungeon.actionManager;
	    	if (!gam.actions.isEmpty())
	        	return;
	    	if (!startNextTurn)
	    		return;
			if ((gam.turnHasEnded) && (!AbstractDungeon.getMonsters().areMonstersBasicallyDead())) {
				startNextTurn = false;
				AbstractDungeon.getCurrRoom().monsters.applyEndOfTurnPowers();
				AbstractDungeon.player.cardsPlayedThisTurn = 0;
				gam.orbsChanneledThisTurn.clear();
				if (ModHelper.isModEnabled("Careless")) {
					Careless.modAction();
				}
				if (ModHelper.isModEnabled("ControlledChaos")) {
					ControlledChaos.modAction();
					AbstractDungeon.player.hand.applyPowers();
				}
				AbstractDungeon.player.applyStartOfTurnRelics();
			    AbstractDungeon.player.applyStartOfTurnPreDrawCards();
				AbstractDungeon.player.applyStartOfTurnCards();
				AbstractDungeon.player.applyStartOfTurnPowers();
				AbstractDungeon.player.applyStartOfTurnOrbs();
				GameActionManager.turn += 1;
				gam.turnHasEnded = false;
				GameActionManager.totalDiscardedThisTurn = 0;
				gam.cardsPlayedThisTurn.clear();
				GameActionManager.damageReceivedThisTurn = 0;
				if ((!AbstractDungeon.player.hasPower("Barricade")) && (!AbstractDungeon.player.hasPower("Blur"))) {
					if (!AbstractDungeon.player.hasRelic("Calipers")) {
						AbstractDungeon.player.loseBlock();
					} else {
						AbstractDungeon.player.loseBlock(15);
					}
				}
				if (!AbstractDungeon.getCurrRoom().isBattleOver) {
					AbstractDungeon.actionManager
							.addToBottom(new DrawCardAction(null, AbstractDungeon.player.gameHandSize, true));
					AbstractDungeon.player.applyStartOfTurnPostDrawRelics();
					AbstractDungeon.player.applyStartOfTurnPostDrawPowers();
					AbstractDungeon.actionManager.addToBottom(new EnableEndTurnButtonAction());
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
		AbstractDungeon.player.limbo.addToBottom(tmp);
		tmp.current_x = card.current_x;
		tmp.current_y = card.current_y;
		tmp.target_x = (Settings.WIDTH / 2.0F - 300.0F * Settings.scale);
		tmp.target_y = (Settings.HEIGHT / 2.0F);
		if (m != null) {
			tmp.calculateCardDamage(m);
		}
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
			AbstractDungeon.player.limbo.addToBottom(tmp);
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
	
	public default String getEnergySymble(PlayerClass c) {
		return " [E] ";
	}
	
	public default String setDescription(PlayerClass c, String... DESCRIPTIONS) {
		String e = this.getEnergySymble(c);
		String ret = DESCRIPTIONS[0];
		for (int i = 1; i < DESCRIPTIONS.length; i++) {
			ret += e + DESCRIPTIONS[i];
		}
		return ret;
	}
	
	public default void rollIntent(AbstractMonster m) {
		ArrayList<AbstractGameAction> actions = new ArrayList<AbstractGameAction>();
		actions.addAll(AbstractDungeon.actionManager.actions);
		m.takeTurn();
    	AbstractDungeon.actionManager.actions.clear();
    	AbstractDungeon.actionManager.actions.addAll(actions);
    	AbstractDungeon.actionManager.addToBottom(new RollMoveAction(m));
    	AbstractDungeon.actionManager.addToBottom(new AbstractGameAction(){
			@Override
			public void update() {
				AbstractDungeon.getMonsters().showIntent();
				this.isDone = true;
			}
    	});
	}
	
	static class CardGlowChanger {
		private static final ArrayList<Color> COLOR_LIST = new ArrayList<Color>();
		private static final HashMap<AbstractCard, ArrayList<Color>> GLOW_COLORS = new HashMap<AbstractCard, ArrayList<Color>>();
		private static final ArrayList<AbstractCard> UPDATE_LIST = new ArrayList<AbstractCard>();
		private static final ArrayList<Integer> USED_COLOR = new ArrayList<Integer>();
		private static final ArrayList<AbstractCard> HARD_GLOW_LOCK = new ArrayList<AbstractCard>();
		
		private static void initialize() {
			Color[] tmp = {Color.GOLD, new Color(0.0F, 1.0F, 0.0F, 0.25F), Color.PINK, Color.SKY, Color.PURPLE, Color.RED, Color.BROWN};
			for (Color c : tmp)
				COLOR_LIST.add(c);
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
			for (AbstractCard c : HARD_GLOW_LOCK) {
				c.beginGlowing();
			}
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
	
	public default boolean canUpdateHandGlow() {
		return AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT;
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
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null
				|| AbstractDungeon.getCurrRoom().monsters == null
				|| AbstractDungeon.getCurrRoom().monsters.monsters == null)
			return false;
		return true;
	}
	
}
