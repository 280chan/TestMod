package cards;

import java.util.HashMap;
import java.util.function.Consumer;
import org.apache.logging.log4j.util.TriConsumer;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AbstractAnonymousCard extends AbstractTestCard {
	private static final HashMap<String, CardStrings> CS = new HashMap<String, CardStrings>();
	private TriConsumer<AbstractAnonymousCard, AbstractPlayer, AbstractMonster> use;
	private Consumer<AbstractAnonymousCard> upgrade;
	private String mcid;
	private int mccost, mcdmg, mcblk, mcmgc;
	private boolean mcexhaust, mcethereal, mcinnate;
	private CardType mctype;
	private CardRarity mcrarity;
	private CardTarget mctarget;
	
	private static CardStrings getStrings(String id) {
		if (!CS.containsKey(id))
			CS.put(id, Strings(id));
		return CS.get(id);
	}
	
	protected static String name(String id) {
		return getStrings(id).NAME;
	}
	
	protected static String desc(String id) {
		return getStrings(id).DESCRIPTION;
	}
	
	protected static String desc1(String id) {
		return getStrings(id).UPGRADE_DESCRIPTION;
	}
	
	protected static String[] extendDesc(String id) {
		return getStrings(id).EXTENDED_DESCRIPTION;
	}
	
	public AbstractAnonymousCard(String id, int cost, CardType type, CardRarity rarity, CardTarget target,
			boolean exhaust, boolean ethereal, boolean innate, int dmg, int blk, int mgc,
			TriConsumer<AbstractAnonymousCard, AbstractPlayer, AbstractMonster> use, Consumer<AbstractAnonymousCard> upgrade) {
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

	public AbstractAnonymousCard(String id, int cost, CardType type, CardRarity rarity, CardTarget target, int dmg,
			int blk, int mgc, TriConsumer<AbstractAnonymousCard, AbstractPlayer, AbstractMonster> use,
			Consumer<AbstractAnonymousCard> upgrade) {
		this(id, cost, type, rarity, target, false, false, false, dmg, blk, mgc, use, upgrade);
	}
	
	public void use(AbstractPlayer p, AbstractMonster m) {
		this.addTmpActionToBot(() -> this.use.accept(this, p, m));
	}
	
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgrade.accept(this);
		}
	}
	
	public AbstractCard makeCopy() {
		return new AbstractAnonymousCard(mcid, mccost, mctype, mcrarity, mctarget, mcexhaust, mcethereal, mcinnate,
				mcdmg, mcblk, mcmgc, use, upgrade);
	}

	public void upgradeMGC(int amount) {
		this.upgradeMagicNumber(amount);
	}
	
	public void upgradeDMG(int amount) {
		this.upgradeDamage(amount);
	}
	
	public void upgradeBLK(int amount) {
		this.upgradeBlock(amount);
	}
	
	public void upgradeCost(int target) {
		this.upgradeBaseCost(target);
	}
	
	public void upgradeDesc(String s) {
		this.rawDescription = s;
		this.initializeDescription();
	}
	
	public void upgradeDesc() {
		upgradeDesc(desc1(this.mcid));
	}
	
}
