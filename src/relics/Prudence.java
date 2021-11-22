package relics;

import java.util.HashMap;
import actions.PlaySpecificCardAction;
import mymod.TestMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class Prudence extends AbstractTestRelic {
	public static final String ID = "Prudence";
	private boolean active;
	private boolean disabledUntilEndOfTurn;
	private static AbstractCard preHoveredCard;
	private static HashMap<AbstractCard, Boolean> canPlay = new HashMap<AbstractCard, Boolean>();
	
	private static Color color = null;
	
	public Prudence() {
		super(RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		for (AbstractCard c : p().hand.group) {
			if (EnergyPanel.totalCount >= c.costForTurn && this.calCanPlay(c)) {
				this.addToGlowChangerList(c, color);
				this.addHardLockGlow(c);
				active = true;
			} else
				this.removeFromGlowList(c, color);
		}
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onVictory() {
		this.stopPulse();
	}
	
	private boolean calCanPlay(AbstractCard c) {
		AbstractMonster m = hoveredMonster();
		if (m == null) {
			m = AbstractDungeon.getMonsters().monsters.stream().filter(c::cardPlayable).findAny().orElse(null);
		}
		return c.cardPlayable(m) && !canPlay(c, m);
	}

	public void onPlayCard(AbstractCard c, AbstractMonster m) {
		if (!this.isActive)
			return;
		canPlay.remove(c);
		if (c == preHoveredCard)
			preHoveredCard = null;
	}
	
	private static boolean isThis(AbstractRelic r) {
		return r instanceof Prudence;
	}
	
	private boolean canPlay(AbstractCard c, AbstractMonster m) {
		if (c.type == CardType.STATUS) {
			if (p().hasRelic("Medical Kit")) {
				return true;
			}
			if (!c.cardID.equals("Slimed")) {
				return false;
			}
		}
		if (c.type == CardType.CURSE) {
			if (p().hasRelic("Blue Candle")) {
				return true;
			}
			if (!c.cardID.equals("Pride")) {
				return false;
			}
		}
		if (AbstractDungeon.actionManager.turnHasEnded) {
			return false;
		} else if (p().hasPower("Entangled") && c.type == CardType.ATTACK) {
			return false;
		} else if (c.freeToPlayOnce) {
			return true;
		}
		for (AbstractRelic r : p().relics)
			if (!isThis(r) && !r.canPlay(c))
				return false;
		for (AbstractBlight b : p().blights)
			if (!b.canPlay(c))
				return false;
		for (AbstractCard card : p().hand.group)
			if (!card.canPlay(c))
				return false;
		if (c.costForTurn <= EnergyPanel.totalCount) {
			return c.canUse(p(), m);
		}
		return false;
	}
	
	private void tryPlay() {
		if (this.active && !this.disabledUntilEndOfTurn) {
			AbstractMonster m = hoveredMonster();
			if (m == null) {
				for (AbstractMonster temp : AbstractDungeon.getCurrRoom().monsters.monsters) {
					if (preHoveredCard.cardPlayable(temp)) {
						m = temp;
						break;
					}
				}
			}
			if (canPlay.get(preHoveredCard) != null && canPlay.get(preHoveredCard)) {
				if (preHoveredCard == null)
					return;
				playCard(preHoveredCard, m);
				canPlay.remove(preHoveredCard);
				preHoveredCard = null;
			} else {
				canPlay.remove(preHoveredCard);
			}
		}
	}
	
	private void saveCanPlay(AbstractCard c) {
		AbstractMonster m = hoveredMonster();
		if (m == null) {
			for (AbstractMonster temp : AbstractDungeon.getCurrRoom().monsters.monsters) {
				if (c.cardPlayable(temp)) {
					m = temp;
					break;
				}
			}
		}
		canPlay.put(c, c.cardPlayable(m) && !canPlay(c, m));
	}
	
	private AbstractMonster hoveredMonster() {
		return AbstractDungeon.getMonsters().hoveredMonster;
	}
	
	private void playCard(AbstractCard c, AbstractMonster hovered) {
		this.addToTop(new PlaySpecificCardAction(hovered, c, true));
		isDone = false;
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		TestMod.setActivity(this);
		this.active = false;
		if (isActive)
			canPlay.clear();
	}

	public void atTurnStart() {
		if (!this.isActive)
			return;
		this.active = true;
		this.disabledUntilEndOfTurn = false;
	}

	public void update() {
		super.update();
		if (!this.isActive)
			return;
		if (this.isActive) {
			if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
				GameActionManager g = AbstractDungeon.actionManager;
				if ((g.cardQueue.size() == 1) && (((CardQueueItem) g.cardQueue.get(0)).isEndTurnAutoPlay)) {
					this.disabledUntilEndOfTurn = true;
				}
				checkPlay();
			}
		}
	}
	
	private void checkPlay() {
		AbstractCard cardFromHotKey = null;
		if (p().hoveredCard != null && p().hoveredCard.costForTurn <= EnergyPanel.totalCount) {
			if (p().isDraggingCard) {
				if (preHoveredCard != p().hoveredCard && !canPlay.containsKey(p().hoveredCard)) {
					preHoveredCard = p().hoveredCard;
					saveCanPlay(preHoveredCard);
				}
			}
		} else if ((cardFromHotKey = InputHelper.getCardSelectedByHotkey(p().hand)) != null) {
			if (cardFromHotKey.costForTurn <= EnergyPanel.totalCount) {
				if (preHoveredCard != cardFromHotKey) {
					preHoveredCard = cardFromHotKey;
				}
			}
		} else if (preHoveredCard != null) {
			tryPlay();
		} else {
			preHoveredCard = null;
		}
	}
}