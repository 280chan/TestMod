package testmod.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.logging.log4j.util.TriConsumer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AnonymousCard extends AbstractTestCard {
	private TriConsumer<AnonymousCard, AbstractPlayer, AbstractMonster> use;
	private Consumer<AnonymousCard> upgrade, init;
	private String mcid;
	private int mccost, mcdmg, mcblk, mcmgc;
	private boolean mcexhaust, mcethereal, mcinnate;
	private CardType mctype;
	private CardRarity mcrarity;
	private CardTarget mctarget;
	private HashMap<String, Function<ArrayList<Object>, Object>> override =
			new HashMap<String, Function<ArrayList<Object>, Object>>();
	private HashMap<String, Boolean> lock = new HashMap<String, Boolean>();
	public boolean absoluteSkip = false;
	private Predicate<AnonymousCard> glow;
	
	public AnonymousCard override(String name, Function<ArrayList<Object>, Object> method) {
		override.put(name, method);
		lock.put(name, true);
		return this;
	}
	
	private void lock(String name) {
		lock.replace(name, !lock.get(name));
	}
	
	public AnonymousCard glow(Predicate<AnonymousCard> p) {
		this.glow = p;
		return this;
	}
	
	public void triggerOnGlowCheck() {
		this.glowColor = glow != null && glow.test(this) ? GOLD_BORDER_GLOW_COLOR.cpy() : BLUE_BORDER_GLOW_COLOR.cpy();
	}
	
	public String[] exD() {
		return extendDesc(this.mcid);
	}
	
	public AnonymousCard(String id, int cost, CardType type, CardRarity rarity, CardTarget target, boolean exhaust,
			boolean ethereal, boolean innate, int dmg, int blk, int mgc,
			TriConsumer<AnonymousCard, AbstractPlayer, AbstractMonster> use, Consumer<AnonymousCard> upgrade) {
		super(id, name(id), cost, desc(id), type, rarity, target);
		this.mcid = id;
		this.mccost = cost;
		this.mctype = type;
		this.mcrarity = rarity;
		this.mctarget = target;
		this.exhaust = this.mcexhaust = exhaust;
		this.isEthereal = this.mcethereal = ethereal;
		this.isInnate = this.mcinnate = innate;
		this.baseDamage = this.mcdmg = dmg;
		this.baseBlock = this.mcblk = blk;
		this.magicNumber = this.baseMagicNumber = this.mcmgc = mgc;
		this.use = use;
		this.upgrade = upgrade;
	}

	public AnonymousCard(String id, int cost, CardType type, CardRarity rarity, CardTarget target, int dmg, int blk,
			int mgc, TriConsumer<AnonymousCard, AbstractPlayer, AbstractMonster> use, Consumer<AnonymousCard> upgrade) {
		this(id, cost, type, rarity, target, false, false, false, dmg, blk, mgc, use, upgrade);
	}
	
	public AnonymousCard(String id, int cost, CardType type, CardRarity rarity, CardTarget target, int dmg, int blk,
			int mgc, TriConsumer<AnonymousCard, AbstractPlayer, AbstractMonster> use, Consumer<AnonymousCard> upgrade,
			Consumer<AnonymousCard> init) {
		this(id, cost, type, rarity, target, false, false, false, dmg, blk, mgc, use, upgrade, init);
	}
	
	public AnonymousCard(String id, int cost, CardType type, CardRarity rarity, CardTarget target, boolean exhaust,
			boolean ethereal, boolean innate, int dmg, int blk, int mgc,
			TriConsumer<AnonymousCard, AbstractPlayer, AbstractMonster> use, Consumer<AnonymousCard> upgrade,
			Consumer<AnonymousCard> init) {
		this(id, cost, type, rarity, target, exhaust, ethereal, innate, dmg, blk, mgc, use, upgrade);
		this.init = init;
		this.init();
	}
	
	public AbstractCard makeCopy() {
		AnonymousCard t = new AnonymousCard(mcid, mccost, mctype, mcrarity, mctarget, mcexhaust, mcethereal, mcinnate,
				mcdmg, mcblk, mcmgc, use, upgrade, init);
		t.override = this.override;
		t.lock = this.lock;
		t.glow = this.glow;
		return t;
	}

	public void init() {
		if (init != null)
			init.accept(this);
	}
	
	public void setAOE() {
		this.isMultiDamage = true;
	}
	
	public void upMGC(int amount) {
		this.upgradeMagicNumber(amount);
	}
	
	public void superMGC(int amount) {
		super.upgradeMagicNumber(amount);
	}
	
	public void upDMG(int amount) {
		this.upgradeDamage(amount);
	}
	
	public void superDMG(int amount) {
		super.upgradeDamage(amount);
	}
	
	public void upBLK(int amount) {
		this.upgradeBlock(amount);
	}
	
	public void superBLK(int amount) {
		super.upgradeBlock(amount);
	}
	
	public void upCost(int target) {
		this.upgradeBaseCost(target);
	}
	
	public void superCost(int target) {
		super.upgradeBaseCost(target);
	}
	
	public void upDesc(String s) {
		this.rawDescription = s;
		this.initializeDescription();
	}
	
	public void upDesc() {
		upDesc(desc1(this.mcid));
	}
	
	public void use(AbstractPlayer p, AbstractMonster m) {
		this.addTmpActionToBot(() -> this.use.accept(this, p, m));
	}
	
	public void upgrade() {
		if (override.containsKey("upgrade") && lock.containsKey("upgrade")) {
			if (lock.get("upgrade")) {
				lock("upgrade");
				override.get("upgrade").apply(this.createList(this));
				lock("upgrade");
			}
		} else if (!this.upgraded) {
			this.upgradeName();
			this.upgrade.accept(this);
		}
	}
	
	public boolean canUpgrade() {
		if (override.containsKey("canUpgrade") && lock.containsKey("canUpgrade") && lock.get("canUpgrade")) {
			lock("canUpgrade");
			boolean tmp = (boolean) override.get("canUpgrade").apply(this.createList(this));
			lock("canUpgrade");
			return tmp;
		}
		return super.canUpgrade();
	}

	public void displayUpgrades() {
		if (override.containsKey("displayUpgrades") && !absoluteSkip) {
			if (lock.get("displayUpgrades")) {
				lock("displayUpgrades");
				override.get("displayUpgrades").apply(this.createList(this));
				lock("displayUpgrades");
			}
		} else {
			super.displayUpgrades();
		}
	}
	
	protected void upgradeDamage(int amount) {
		if (override.containsKey("upgradeDamage")) {
			if (lock.get("upgradeDamage")) {
				lock("upgradeDamage");
				override.get("upgradeDamage").apply(this.createList(this, amount));
				lock("upgradeDamage");
			}
		} else {
			super.upgradeDamage(amount);
		}
	}
	
	protected void upgradeBlock(int amount) {
		if (override.containsKey("upgradeBlock")) {
			if (lock.get("upgradeBlock")) {
				lock("upgradeBlock");
				override.get("upgradeBlock").apply(this.createList(this, amount));
				lock("upgradeBlock");
			}
		} else {
			super.upgradeBlock(amount);
		}
	}
	
	protected void upgradeMagicNumber(int amount) {
		if (override.containsKey("upgradeMagicNumber")) {
			if (lock.get("upgradeMagicNumber")) {
				lock("upgradeMagicNumber");
				override.get("upgradeMagicNumber").apply(this.createList(this, amount));
				lock("upgradeMagicNumber");
			}
		} else {
			super.upgradeMagicNumber(amount);
		}
	}
	
	public void triggerOnEndOfPlayerTurn() {
		if (override.containsKey("triggerOnEndOfPlayerTurn") && !absoluteSkip) {
			if (lock.get("triggerOnEndOfPlayerTurn")) {
				lock("triggerOnEndOfPlayerTurn");
				override.get("triggerOnEndOfPlayerTurn").apply(this.createList(this));
				lock("triggerOnEndOfPlayerTurn");
			}
		} else {
			super.triggerOnEndOfPlayerTurn();
		}
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		if (override.containsKey("calculateCardDamage") && !absoluteSkip) {
			if (lock.get("calculateCardDamage")) {
				lock("calculateCardDamage");
				override.get("calculateCardDamage").apply(this.createList(this, m));
				lock("calculateCardDamage");
			}
		} else {
	    	super.calculateCardDamage(m);
		}
    }
	
	// TODO 重写
	
}
