package powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnPlayerDeathPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;

import cards.mahjong.AbstractMahjongCard;
import cards.mahjong.MahjongZs;
import mymod.TestMod;
import relics.Mahjong;

public class MahjongZsPower extends AbstractTestPower implements OnPlayerDeathPower {
	public static final String POWER_ID = "MahjongZsPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final String[] DESC0 = split(0, 6);
	private static final String[] DESC1 = split(6, 12);
	private static final String[] Z3_DESC = split(12, 14);
	private static final String[] Z4_DESC = split(14, 16);
	
	public static final int BASE_HEAL = 1;
	public static final double BASE_ATK_BONUS = 100.0;
	public static final int REGEN = 1;
	
	private int num;
	private double atkBonus = BASE_ATK_BONUS;
	private int heal = BASE_HEAL;
	
	private static String[] split(int start, int end) {
		String[] tmp = new String[end - start];
		for (int i = 0; i < tmp.length; i++)
			tmp[i] = DESCRIPTIONS[start + i];
		return tmp;
	}
	
	public MahjongZsPower(AbstractCreature owner, MahjongZs card, int amount) {
		super(POWER_ID + card.cardID);
		this.num = card.num();
		this.name = NAME + "[" + card.name + "]";
		this.owner = owner;
		this.amount = amount;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		int i = this.num - 1;
		if (this.num > 5)
			i--;
		this.description = DESC0[i] + this.amount + DESC1[i];
		if (this.num == 3)
			this.description += Z3_DESC[0] + this.atkBonusDisplay() + Z3_DESC[1];
		else if (this.num == 4)
			this.description += Z4_DESC[0] + this.heal + Z4_DESC[1];
	}
	
	private String atkBonusDisplay() {
		int i = (int) atkBonus;
		if (atkBonus - i == 0)
			return "" + i;
		double j = ((int) (100 * atkBonus + 0.5)) / 100.0;
		return "" + j;
	}
	
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card instanceof AbstractMahjongCard) {
			if (this.num == 1) {
				this.addToBot(new ApplyPowerAction(this.owner, this.owner, new RegenPower(this.owner, REGEN), REGEN));
				this.addToBot(new ApplyPowerAction(this.owner, this.owner, new PlatedArmorPower(this.owner, this.amount), this.amount));
				return;
			} else if (this.num == 2) {
				this.addToBot(new ApplyPowerAction(this.owner, this.owner, new MantraPower(this.owner, this.amount), this.amount));
				return;
			} else if (this.num == 6) {
				this.addToBot(new AbstractGameAction(){
					@Override
					public void update() {
						this.isDone = true;
						int num = 0;
						for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
							if (!m.isDead && !m.escaped)
								num++;
						num *= MahjongZsPower.this.amount;
						if (num > 0)
						    AbstractDungeon.player.gainGold(num);
					}});
				return;
			}
		}
		if (this.num == 4) {
			this.heal++;
			this.updateDescription();
		} else if (this.num == 3 && card.type != CardType.ATTACK) {
			this.atkBonus += this.atkBonus * this.amount / 100.0;
			this.updateDescription();
		}
	}
	
	public float atDamageGive(float damage, DamageType type) {
		if (this.num == 3 && type == DamageType.NORMAL)
			return (float) (damage * this.atkBonus / 100.0);
		return damage;
	}

	@Override
	public boolean onPlayerDeath(AbstractPlayer p, DamageInfo info) {
		if (this.num == 4 && this.amount > 0) {
			p.currentHealth = 0;
			int hp = (int) (this.heal * p.maxHealth / 100.0);
			if (hp < 1)
				hp = 1;
			p.heal(hp);
			this.amount--;
			if (this.amount == 0)
				this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
			this.heal = BASE_HEAL;
			this.updateDescription();
			return false;
		}
		return true;
	}
	
	public void atStartOfTurn() {
		AbstractPlayer p = AbstractDungeon.player;
		if (this.num == 7 && p.hasRelic(TestMod.makeID(Mahjong.ID))) {
			for (int i = 0; i < this.amount; i++) {
				this.addToBot(new AbstractGameAction(){
					@Override
					public void update() {
						this.isDone = true;
						p.getRelic(TestMod.makeID(Mahjong.ID)).atTurnStart();
					}
				});
			}
		}
	}

}
