package testmod.relicsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

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

public class EvilDaggerUp extends AbstractUpgradedRelic {
	private static ArrayList<AbstractMonster> killed = new ArrayList<AbstractMonster>();
	private static ArrayList<AbstractCard> CARDS = new ArrayList<AbstractCard>();
	private static final Color COLOR = Color.SCARLET.cpy();
	private static boolean used = false;
	private static com.megacrit.cardcrawl.random.Random rng;
	
	public EvilDaggerUp() {
		this.counter = 1;
	}
	
	public String getUpdatedDescription() {
		if (this.inCombat() && CARDS.size() > 0) {
			return DESCRIPTIONS[0] + DESCRIPTIONS[1]
					+ CARDS.stream().map(c -> " #y[" + c.name + "]").reduce((a, b) -> a + "," + b).get()
					+ DESCRIPTIONS[2];
		}
		return DESCRIPTIONS[0];
	}
	
	public Consumer<AbstractMonster> doSth(AbstractCard c) {
		return m -> {
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
					AbstractDungeon.effectList
							.add(new GainPennyEffect(p(), m.hb.cX, m.hb.cY, p().hb.cX, p().hb.cY, true));
			};
			lambda.add(a);
			Collections.shuffle(lambda, new Random(AbstractDungeon.cardRandomRng.randomLong()));
			lambda.forEach(l -> l.run());
		};
	}
	
	public void onUseCard(AbstractCard c, UseCardAction a) {
		if (!this.isActive)
			return;
		this.stupidDevToBot(() -> {
			if (!killed.isEmpty() && CARDS.contains(c)) {
				this.relicStream(EvilDaggerUp.class).forEach(r -> killed.forEach(doSth(c)));
				show();
				c.superFlash(COLOR);
				used = true;
			}
			killed.clear();
		});
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		used = false;
		CARDS.clear();
		ArrayList<AbstractCard> list = this.combatCards().filter(c -> c.type == CardType.ATTACK).collect(toArrayList());
		if (!list.isEmpty()) {
			Collections.shuffle(list, new Random(AbstractDungeon.cardRandomRng.randomLong()));
			list.stream().limit(this.counter).forEach(CARDS::add);
		}
		this.updateDescription();
	}
	
	public void onVictory() {
		if (!used)
			this.counter++;
		CARDS.clear();
		killed.clear();
		this.updateDescription();
		this.stopPulse();
    }
	
	public void onMonsterDeath(final AbstractMonster m) {
		if (this.isActive && !m.hasPower("Minion") && (m.isDead || m.isDying)) {
			killed.add(m);
		}
    }
	
	public void onRefreshHand() {
		if (this.isActive && this.inCombat())
			this.updateHandGlow();
	}
	
	private boolean check(AbstractCard c) {
		if (rng == null)
			rng = MISC.copyRNG(AbstractDungeon.monsterRng);
		return p().hand.group.contains(c) && c.hasEnoughEnergy()
				&& c.cardPlayable(AbstractDungeon.getMonsters().getRandomMonster(null, true, rng));
	}
	
	private void addGlow(AbstractCard c) {
		this.addToGlowChangerList(c, COLOR);
		this.beginLongPulse();
	}
	
	private void updateHandGlow() {
		this.stopPulse();
		CARDS.stream().filter(this::check).forEach(this::addGlow);
		CARDS.stream().filter(not(this::check)).forEach(c -> this.removeFromGlowList(c, COLOR));
	}

}