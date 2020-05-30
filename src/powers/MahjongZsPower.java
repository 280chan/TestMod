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
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;

import cards.mahjong.AbstractMahjongCard;
import cards.mahjong.MahjongZs;
import mymod.TestMod;
import relics.Mahjong;

public class MahjongZsPower extends AbstractPower implements OnPlayerDeathPower {
	public static final String POWER_ID = "MahjongZsPower";
	public static final String NAME = "麻将";
	public static final String IMG = TestMod.powerIMGPath(POWER_ID);
	public static final String[] DESCRIPTIONS = { "每当你打出 #b1 张 #y麻将 牌，获得 #b1 层 #y再生 ， #b", "每当你打出 #b1 张 #y麻将 牌，获得 #b",
			"每当你打出 #b1 张 #r非 #y攻击 牌，你造成的攻击伤害提高 #b", "免疫 #b", "每打出 #b1 张 #y麻将 牌，场上每有 #b1 个敌人，获得 #b", "每回合开始额外触发 #b"};
	public static final String[] DESCRIPTIONS1 = { " 层 #y多层护甲 。", " 层 #y真言 。", "% 。加成之间相乘。",
			" 次致命伤害，以 #b1% 最大生命复活。每打出 #b1 张牌将复活生命比例提升 #b1% ，生效后重置。", " #y金币 。", " 次麻将遗物。"};
	public static final String[] Z3_POST_DESC = { "(造成 #b", "% 攻击伤害)" };
	public static final String[] Z4_POST_DESC = { "(生效时复活 #b", "% 生命)" };
	
	public static final int BASE_HEAL = 1;
	public static final double BASE_ATK_BONUS = 100.0;
	public static final int REGEN = 1;
	
	private int num;
	private double atkBonus = BASE_ATK_BONUS;
	private int heal = BASE_HEAL;
	
	public MahjongZsPower(AbstractCreature owner, MahjongZs card, int amount) {
		this.num = card.num();
		this.name = NAME + "[" + card.name + "]";
		this.ID = POWER_ID + card.cardID;
		this.owner = owner;
		this.amount = amount;
		this.img = ImageMaster.loadImage(IMG);
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	public void updateDescription() {
		int i = this.num - 1;
		if (this.num > 5)
			i--;
		this.description = DESCRIPTIONS[i] + this.amount + DESCRIPTIONS1[i];
		if (this.num == 3)
			this.description += Z3_POST_DESC[0] + this.atkBonusDisplay() + Z3_POST_DESC[1];
		else if (this.num == 4)
			this.description += Z4_POST_DESC[0] + this.heal + Z4_POST_DESC[1];
	}
	
	private String atkBonusDisplay() {
		int i = (int) atkBonus;
		if (atkBonus - i == 0)
			return "" + i;
		double j = ((int) (100 * atkBonus + 0.5)) / 100.0;
		return "" + j;
	}
	
	public void stackPower(final int stackAmount) {
		this.fontScale = 8.0f;
        this.amount += stackAmount;
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
		if (this.num == 7 && p.hasRelic(Mahjong.ID)) {
			for (int i = 0; i < this.amount; i++) {
				this.addToBot(new AbstractGameAction(){
					@Override
					public void update() {
						this.isDone = true;
						p.getRelic(Mahjong.ID).atTurnStart();
					}
				});
			}
		}
	}

}
