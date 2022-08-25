package testmod.relicsup;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import testmod.relics.AaSapphireKey;
import testmod.relics.DeterminationOfClimber;

public class DeterminationOfClimberUp extends AbstractUpgradedRelic {
	private static Color color = null;
	public static RewardItem TMP;
	private static Random rng;

	public static Color setColorIfNull(Supplier<Color> c) {
		if (color == null)
			color = c.get();
		return color;
	}
	
	private void initColor() {
		if (color == null)
			color = DeterminationOfClimber.setColorIfNull(this::initGlowColor);
	}
	
	public void act(int count) {
		this.atb(new GainEnergyAction(count));
		this.atb(new HealAction(p(), p(), count));
		this.atb(new DrawCardAction(p(), count));
		this.atb(new GainGoldAction(count));
		this.atb(new DamageAllEnemiesAction(p(), DamageInfo.createDamageMatrix(count, true), DamageType.THORNS,
				AttackEffect.BLUNT_LIGHT));
		this.show();
	}
	
	private int getValue(AbstractCard c) {
		return (c.freeToPlay() || c.cost == -2) ? 0 : (c.cost == -1 ? EnergyPanel.totalCount : c.costForTurn);
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (!c.isInAutoplay) {
			if (this.counter > -1) {
				int amount = this.getValue(c);
				if (amount > this.counter)
					this.act(amount - this.counter);
			}
			this.counter = this.getValue(c);
		}
	}
	
	public void onRefreshHand() {
		this.initColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		if (!this.inCombat())
			return;
		this.stopPulse();
		if (rng == null)
			rng = AbstractDungeon.monsterRng.copy();
		colorRegister(color).addRelic(this).addPredicate(c -> this.getValue(c) > this.counter && this.counter != -1 &&
				c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getMonsters().getRandomMonster(null, true, rng)))
				.updateHand();
	}

	public void onVictory() {
		this.stopPulse();
	}
	
	public void onChestOpenAfter(boolean boss) {
		AbstractRoom r;
		if (!boss && AbstractDungeon.currMapNode != null && ((r = AbstractDungeon.getCurrRoom()) != null)
				&& r instanceof TreasureRoom) {
			r.rewards.stream().filter(a -> a.type == RewardType.RELIC).forEach(a -> a.relic = this.tryUpgrade(a.relic));
			
			RewardItem i = new RewardItem(TMP == null ? TMP = new RewardItem() : TMP, RewardType.SAPPHIRE_KEY);
			RewardItem j = new RewardItem(i, RewardType.SAPPHIRE_KEY);
			i.relicLink = j;
			i.relic = AaSapphireKey.PRE;
			j.relic = AaSapphireKey.KEY;
			
			r.rewards.add(i);
			r.rewards.add(j);
		}
	}
	
	@SpirePatch(clz = RewardItem.class, method = "renderRelicLink")
	public static class RemoveRenderLinkPatch {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(RewardItem r) {
			return r.relic == AaSapphireKey.PRE ? SpireReturn.Return() : SpireReturn.Continue();
		}
	}
	
}