package testmod.relics;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import testmod.mymod.TestMod;

public class EvilDagger extends AbstractTestRelic {
	private ArrayList<AbstractMonster> killed = new ArrayList<AbstractMonster>();
	private AbstractCard c;
	private static final Color COLOR = Color.SCARLET.cpy();
	
	public String getUpdatedDescription() {
		if (this.inCombat()) {
			return DESCRIPTIONS[0] + DESCRIPTIONS[1] + (this.c == null ? "null" : c.name) + DESCRIPTIONS[2];
		}
		return DESCRIPTIONS[0];
	}
	
	public void doSth(AbstractCard c, AbstractMonster m) {
		ArrayList<Lambda> lambda = new ArrayList<Lambda>();
		Lambda a = () -> p().increaseMaxHp(c.upgraded ? 4 : 3, true);
		lambda.add(a);
		a = () -> {
			ArrayList<AbstractCard> list = p().masterDeck.group.stream().filter(i -> i.canUpgrade())
					.collect(toArrayList());
			if (!list.isEmpty()) {
				AbstractCard t = list.get(AbstractDungeon.miscRng.random(0, list.size() - 1));
				t.upgrade();
				p().bottledCardUpgradeCheck(t);
				AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F,
						Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(t.makeStatEquivalentCopy()));
			}
		};
		lambda.add(a);
		a = () -> {
			p().gainGold(c.upgraded ? 25 : 20);
			for (int i = 0; i < (c.upgraded ? 25 : 20); i++)
				AbstractDungeon.effectList.add(new GainPennyEffect(p(), m.hb.cX, m.hb.cY, p().hb.cX, p().hb.cY, true));
		};
		lambda.add(a);
		TestMod.randomItem(lambda, AbstractDungeon.cardRandomRng).run();
	}
	
	public void onUseCard(AbstractCard c, UseCardAction a) {
		if (!this.isActive)
			return;
		this.stupidDevToBot(() -> {
			if (!killed.isEmpty() && c.equals(this.c)) {
				this.relicStream(EvilDagger.class).forEach(r -> killed.forEach(m -> doSth(c, m)));
				show();
				c.superFlash(COLOR);
			}
			killed.clear();
		});
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		ArrayList<AbstractCard> list = this.combatCards().filter(c -> c.type == CardType.ATTACK).collect(toArrayList());
		if (!list.isEmpty())
			this.c = TestMod.randomItem(list, AbstractDungeon.cardRandomRng);
		this.updateDescription();
	}
	
	public void onVictory() {
		this.killed.clear();
		this.updateDescription();
		this.stopPulse();
	}
	
	public void onMonsterDeath(final AbstractMonster m) {
		if (this.isActive && !m.hasPower("Minion") && (m.isDead || m.isDying)) {
			this.killed.add(m);
		}
	}
	
	public void onRefreshHand() {
		if (!this.isActive)
			return;
		if (this.inCombat())
			this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		if (p().hand.group.stream().anyMatch(c -> c.equals(this.c) && c.hasEnoughEnergy()
				&& c.cardPlayable(this.randomMonster()))) {
			this.addToGlowChangerList(this.c, COLOR);
			this.beginLongPulse();
		} else if (this.c != null) {
			this.removeFromGlowList(this.c, COLOR);
			this.stopPulse();
		}
	}

}