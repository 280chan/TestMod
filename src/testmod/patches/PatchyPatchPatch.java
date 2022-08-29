package testmod.patches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatches2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import testmod.relics.PatchyPatch;
import testmod.utils.MiscMethods;

@SpirePatch(clz = com.megacrit.cardcrawl.core.CardCrawlGame.class, method = "render")
public class PatchyPatchPatch implements MiscMethods {
	private static final String[] ANNOTATION = { SpirePatch.class.getName(), SpirePatches.class.getName(),
			SpirePatch2.class.getName(), SpirePatches2.class.getName() };
	public static final String VAL = "testmodPatchyPatch";
	
	public static void Raw(CtBehavior ctBehavior) {
		ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
		Patcher.annotationDBMap.values().forEach(db -> {
			if (db == null)
				return;
			ArrayList<String> patchClasses = new ArrayList<String>();
			
			Arrays.stream(ANNOTATION).forEach(i -> {
				Map<String, Set<String>> a = db.getAnnotationIndex();
				if (a != null) {
					Set<String> s = a.get(i);
					if (s != null)
						patchClasses.addAll(s);
				}
			});
			
			patchClasses.stream().filter(cn -> cn != null).forEach(className -> {
				try {
					CtClass ctPatchClass = pool.get(className);
					CtClass relic = pool.get(PatchyPatch.class.getName());
					Stream.of(ctPatchClass.getDeclaredMethods()).filter(i -> isPatch(i)).forEach(m -> {
						try {
							m.addLocalVariable(VAL, relic);
							m.insertBefore("{if(" + CardCrawlGame.class.getName() + ".dungeon != null && " + 
									AbstractDungeon.class.getName() + ".player != null) {" + VAL + " = (" + 
									PatchyPatch.class.getName() +")" +  AbstractDungeon.class.getName() +
									".player.getRelic(\"testmod-PatchyPatch\");if(" + VAL + " != null){" + VAL + 
									".patchAttack(\"" + m.getLongName() + "\");}}}" );
						} catch (CannotCompileException e) {
							e.printStackTrace();
						}
					});
				} catch (NotFoundException e1) {
					e1.printStackTrace();
				}
			});
		});
    }

	private static boolean isPatch(CtMethod m) {
		return isPrefix(m) || isPostfix(m) || (!isLocator(m) && isInsert(m));
	}

	private static boolean isPrefix(CtMethod m) {
		return "Prefix".equals(m.getName()) || m.hasAnnotation(SpirePrefixPatch.class);
	}

	private static boolean isPostfix(CtMethod m) {
		return "Postfix".equals(m.getName()) || m.hasAnnotation(SpirePostfixPatch.class);
	}

	private static boolean isLocator(CtMethod m) {
		return "Locator".equals(m.getName()) || m.getName().contains("Locator");
	}

	private static boolean isInsert(CtMethod m) {
		return "Insert".equals(m.getName()) || m.hasAnnotation(SpireInsertPatch.class);
	}

}
