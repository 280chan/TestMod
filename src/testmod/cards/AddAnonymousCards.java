package testmod.cards;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.cards.blue.WhiteNoise;
import com.megacrit.cardcrawl.cards.colorless.Discovery;
import com.megacrit.cardcrawl.cards.green.Distraction;
import com.megacrit.cardcrawl.cards.red.InfernalBlade;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AngryPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import testmod.actions.AdversityCounterattackAction;
import testmod.actions.ComboMasterAction;
import testmod.mymod.TestMod;
import testmod.powers.AssimilatedRunePower;
import testmod.powers.BloodBladePower;
import testmod.powers.ChaoticCorePower;
import testmod.powers.ConditionedReflexPower;
import testmod.powers.DisillusionmentEchoPower;
import testmod.powers.EnhanceArmermentPower;
import testmod.powers.FightingIntentionPower;
import testmod.powers.PlagueActPower;
import testmod.powers.PlaguePower;
import testmod.powers.PulseDistributorPower;
import testmod.utils.MiscMethods;

public class AddAnonymousCards implements MiscMethods {
	private static void add(AnonymousCard c) {
		TestMod.add(c);
	}
	public void add() {
		add(new AnonymousCard("AdversityCounterattack",
				(c, p, m) -> addTmpActionToTop(apply(p, new ArtifactPower(m, c.magicNumber)),
						apply(p, new VulnerablePower(p, c.magicNumber, false)),
						apply(p, new IntangiblePlayerPower(p, 1)),
						new AdversityCounterattackAction(p, m, AttackEffect.SLASH_HORIZONTAL)),
				c -> c.upMGC(1)));
		add(new AnonymousCard("AssimilatedRune", (c, player, m) -> {
			AssimilatedRunePower p = new AssimilatedRunePower(player, c.magicNumber, c.upgraded);
			if (player.hasPower(p.ID)) {
				p = (AssimilatedRunePower) player.getPower(p.ID);
				p.stackPower(c.magicNumber);
				p.updateDescription();
			} else {
				player.powers.add(0, p);
			}
		}, c -> c.upDesc()));
		add(new AnonymousCard("BackupPower", (c, p, m) -> att(new GainEnergyAction(c.magicNumber)), c -> c.upMGC(1),
				c -> c.upDesc(c.exD()[0] + MISC.energyString(c.magicNumber) + c.exD()[1])) {
		}.override("upgradeMagicNumber", l -> {
			AnonymousCard c = (AnonymousCard) l.get(0);
			int n = (int) l.get(1);
			c.superMGC(n);
			c.init();
			return null;
		}).override("triggerOnEndOfPlayerTurn", l -> {
			AnonymousCard c = (AnonymousCard) l.get(0);
			AbstractPlayer p = AbstractDungeon.player;
			atb(apply(p, new EnergizedPower(p, 1)));
			c.absoluteSkip = true;
			c.triggerOnEndOfPlayerTurn();
			c.absoluteSkip = false;
			return null;
		}));
		add(new AnonymousCard("BloodBlade", (c, p, m) -> {
			if (BloodBladePower.hasThis(c.upgraded)) {
				BloodBladePower.getThis(c.upgraded).onFirstGain();
			} else {
				att(apply(p, new BloodBladePower(p, c.upgraded)));
			}
		}, c -> c.upDesc()));
		add(new AnonymousCard("ChaoticCore", (c, p, m) -> this.addTmpActionToTop(new IncreaseMaxOrbAction(1),
				apply(p, new ChaoticCorePower(p, c.magicNumber))), c -> c.upMGC(1)));
		add(new AnonymousCard("Collector", (c, p, m) -> {
			AttackEffect e = AttackEffect.SLASH_DIAGONAL;
			if (c.damage > 40)
				e = AttackEffect.SLASH_HEAVY;
			else if (c.damage > 20)
				e = AttackEffect.SLASH_HORIZONTAL;
			if (c.multiDamage == null)
				c.applyPowers();
			att(new DamageAllEnemiesAction(p, c.multiDamage, c.damageTypeForTurn, e));
		}, c -> c.upMGC(1), c -> {
			c.setAOE();
			c.misc = c.baseDamage;
		}).override("calculateCardDamage", l -> {
			AnonymousCard c = (AnonymousCard) l.get(0);
			AbstractMonster m = (AbstractMonster) l.get(1);
			c.baseDamage = c.misc + c.magicNumber * p().relics.size();
			c.absoluteSkip = true;
			c.calculateCardDamage(m);
			c.absoluteSkip = false;
			return null;
		}));
		add(new AnonymousCard("ComboMaster", (c, p, m) -> {
			addTmpActionToTop(() -> GetAllInBattleInstances.get(c.uuid).stream().map(a -> (AnonymousCard) a)
					.forEach(a -> a.init()));
			att(new ComboMasterAction(p, c.multiDamage, c.magicNumber, c.damageTypeForTurn));
		}, c -> {
			c.init();
			c.upDesc();
		}, c -> {
			if (c.misc == 0) {
				c.misc = 1;
				c.setAOE();
			} else if (!c.upgraded) {
				c.upDMG(c.misc);
				c.upMGC(c.misc);
			} else {
				c.upDMG(c.misc);
				c.upMGC(c.misc);
				c.misc = 2;
			}
		}));
		add(new AnonymousCard("ConditionedReflex",
				(c, p, m) -> att(apply(p, new ConditionedReflexPower(p, c.magicNumber))), c -> c.upMGC(1)));
		add(new AnonymousCard("DisillusionmentEcho", (c, p, m) -> this.addTmpXCostActionToTop(c, a -> {
			int amount = a / (c.magicNumber == 0 ? 3 - c.timesUpgraded : c.magicNumber);
			if (amount > 0)
				att(apply(p, new DisillusionmentEchoPower(p, amount)));
		}), c -> c.upMGC(-1)));
		
		add(new AnonymousCard("Dream",
				(c, p, m) -> Stream.of(new Discovery(), new WhiteNoise(), new Distraction(), new InfernalBlade())
						.peek(a -> a.costForTurn = 0).map(MakeTempCardInHandAction::new).forEach(this::att),
				c -> {
					c.upDesc();
					c.exhaust = false;
				}, c -> MultiCardPreview.add(c, new InfernalBlade(), new Distraction(), new WhiteNoise(),
						new Discovery())));
		add(new AnonymousCard("EnhanceArmerment",
				(c, p, m) -> att(apply(p, new EnhanceArmermentPower(p, c.magicNumber))), c -> c.upCost(0)));
		add(new AnonymousCard("FightingIntention",
				(c, p, m) -> att(apply(p, new FightingIntentionPower(p, c.magicNumber))), c -> c.upCost(2)));
		add(new AnonymousCard("HeadAttack", (c, p, m) -> {
			att(new DrawCardAction(p, c.magicNumber));
			this.rollIntentAction(m);
		}, c -> c.upMGC(1)));
		add(new AnonymousCard("Illusory", (a, p, m) -> {
			addTmpActionToTop(() -> p.drawPile.group.stream().filter(c -> !c.isEthereal).forEach(c -> {
				c.isEthereal = true;
				c.name += a.exD()[0];
			}));
			addTmpActionToTop(() -> {
				int e = (int) p.drawPile.group.stream().filter(c -> c.isEthereal).count();
				if (e > 0)
					att(new GainEnergyAction(e));
			});
			att(new DrawCardAction(p, a.magicNumber));
		}, c -> c.upMGC(1)).glow(a -> p().drawPile.group.stream().anyMatch(c -> c.isEthereal)));
		add(new AnonymousCard("Plague", (c, p, m) -> {
			att(apply(p, new PlagueActPower(p, c.magicNumber)));
			this.addTmpXCostActionToTop(c, e -> att(apply(p, new PlaguePower(p, e))));
		}, c -> {
			c.upDesc();
			c.isEthereal = false;
		}));
		add(new AnonymousCard("Provocation", (c, p, m) -> {
			if (!m.hasPower(AngryPower.POWER_ID))
				att(apply(p, new AngryPower(m, 1)));
			att(apply(p, new StrengthPower(p, c.magicNumber)));
		}, c -> c.upMGC(3)));
		add(new AnonymousCard("PulseDistributor", (c, p, m) -> {
			if (PulseDistributorPower.hasThis(p)) {
				PulseDistributorPower po = PulseDistributorPower.getThis(p);
				if (po.magic > c.magicNumber) {
					this.addTmpActionToTop(new RemoveSpecificPowerAction(p, p, po),
							apply(p, new PulseDistributorPower(p, c.magicNumber, po.DAMAGES)));
				}
			} else {
				att(apply(p, new PulseDistributorPower(p, c.magicNumber)));
			}
		}, c -> c.upMGC(-1), c -> c.upDesc(c.exD()[0] + (c.magicNumber == 0 ? "" : ((c.magicNumber > 0 ? "+" : "") +
				c.magicNumber)) + c.exD()[1]))
		.override("upgradeMagicNumber", l -> {
			AnonymousCard c = (AnonymousCard) l.get(0);
			int n = (int) l.get(1);
			c.superMGC(n);
			c.init();
			return null;
		}));
		
		
		// TODO
	}
}
