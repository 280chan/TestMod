package relics;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import actions.PlaySpecificCardAction;
import mymod.TestMod;
import utils.MiscMethods;

public class Prudence extends MyRelic implements MiscMethods {
	public static final String ID = "Prudence";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "你可以在你的回合打出一切由于能量不足以外原因而不能被打出的牌。如果是 #y状态 或 #y诅咒 ，将其 #y消耗 。(有许多bug被宽容了)";//遗物效果的文本描叙。
	
	public static AbstractPlayer p;
	private boolean active;
	private boolean disabledUntilEndOfTurn;
	private static AbstractCard preHoveredCard;
	private static HashMap<AbstractCard, Boolean> canPlay = new HashMap<AbstractCard, Boolean>();
	
	private static Color color = null;
	
	public Prudence() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT)
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
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
			for (AbstractMonster temp : AbstractDungeon.getCurrRoom().monsters.monsters) {
				if (c.cardPlayable(temp)) {
					m = temp;
					break;
				}
			}
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
	
	public static boolean hasThis() {
		return AbstractDungeon.player.hasRelic(ID);
	}
	
	private boolean canPlay(AbstractCard c, AbstractMonster m) {
		if (c.type == CardType.STATUS) {
			if (p.hasRelic("Medical Kit")) {
				return true;
			}
			if (!c.cardID.equals("Slimed")) {
				return false;
			}
		}
		if (c.type == CardType.CURSE) {
			if (p.hasRelic("Blue Candle")) {
				return true;
			}
			if (!c.cardID.equals("Pride")) {
				return false;
			}
		}
		if (AbstractDungeon.actionManager.turnHasEnded) {
			return false;
		} else if (p.hasPower("Entangled") && c.type == CardType.ATTACK) {
			return false;
		} else if (c.freeToPlayOnce) {
			return true;
		}
		for (AbstractRelic r : p.relics)
			if (!r.relicId.equals(ID) && !r.canPlay(c))
				return false;
		for (AbstractBlight b : p.blights)
			if (!b.canPlay(c))
				return false;
		for (AbstractCard card : p.hand.group)
			if (!card.canPlay(c))
				return false;
		if (c.costForTurn <= EnergyPanel.totalCount) {
			return c.canUse(p, m);
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
		return AbstractDungeon.getCurrRoom().monsters.hoveredMonster;
	}
	
	private void playCard(AbstractCard c, AbstractMonster hovered) {
		AbstractDungeon.actionManager.addToBottom(new PlaySpecificCardAction(hovered, c, true));
		isDone = false;
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		p = AbstractDungeon.player;
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
				if (p == null) {
					p = AbstractDungeon.player;
				}
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
		if (p.hoveredCard != null && p.hoveredCard.costForTurn <= EnergyPanel.totalCount) {
			if (p.isDraggingCard) {
				if (preHoveredCard != p.hoveredCard && !canPlay.containsKey(p.hoveredCard)) {
					preHoveredCard = p.hoveredCard;
					saveCanPlay(preHoveredCard);
				}
			}
		} else if ((cardFromHotKey = InputHelper.getCardSelectedByHotkey(p.hand)) != null) {
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