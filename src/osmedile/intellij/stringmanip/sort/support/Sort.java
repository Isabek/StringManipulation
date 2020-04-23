package osmedile.intellij.stringmanip.sort.support;

import osmedile.intellij.stringmanip.sort.support.Paour.NaturalOrderComparator;

import java.math.BigInteger;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.*;

public enum Sort {

	SHUFFLE(new Comparator<SortLine>() {

		@Override
		public int compare(SortLine o1, SortLine o2) {
			throw new RuntimeException();
		}
	}),
	REVERSE(new Comparator<SortLine>() {
		@Override
		public int compare(SortLine o1, SortLine o2) {
			throw new RuntimeException();
		}
	}),
	CASE_SENSITIVE_A_Z(new ComparatorAdapterFactory() {
		public Comparator<SortLine> adapter(Comparator<String> comparator) {
			return new Comparator<SortLine>() {
				@Override
				public int compare(SortLine o1, SortLine o2) {
					if (comparator == null) {
						return o1.getTextForComparison().compareTo(o2.getTextForComparison());
					}
					return comparator.compare(o1.getTextForComparison(), o2.getTextForComparison());
				}
			};
		}
	}),
	CASE_SENSITIVE_Z_A(new ComparatorAdapterFactory() {
		public Comparator<SortLine> adapter(Comparator<String> comparator) {
			return new Comparator<SortLine>() {
				@Override
				public int compare(SortLine o1, SortLine o2) {
					if (comparator == null) {
						return o2.getTextForComparison().compareTo(o1.getTextForComparison());
					}

					return comparator.compare(o2.getTextForComparison(), o1.getTextForComparison());
				}
			};
		}
	}),
	CASE_INSENSITIVE_A_Z(new ComparatorAdapterFactory() {
		public Comparator<SortLine> adapter(Comparator<String> comparator) {
			return new Comparator<SortLine>() {
				@Override
				public int compare(SortLine o1, SortLine o2) {
					if (comparator == null) {
						return o1.getTextForComparison().compareToIgnoreCase(o2.getTextForComparison());
					}
					return comparator.compare(o1.getTextForComparison().toLowerCase(), o2.getTextForComparison().toLowerCase());
				}
			};
		}
	}),
	CASE_INSENSITIVE_Z_A(new ComparatorAdapterFactory() {
		public Comparator<SortLine> adapter(Comparator<String> comparator) {
			return new Comparator<SortLine>() {
				@Override
				public int compare(SortLine o1, SortLine o2) {
					if (comparator == null) {
						return o2.getTextForComparison().compareToIgnoreCase(o1.getTextForComparison());
					}
					return comparator.compare(o2.getTextForComparison().toLowerCase(), o1.getTextForComparison().toLowerCase());
				}
			};
		}
	}),
	LINE_LENGTH_SHORT_LONG(new Comparator<SortLine>() {
		@Override
		public int compare(SortLine o1, SortLine o2) {
			return o1.getTextForComparison().length() - o2.getTextForComparison().length();
		}
	}),
	LINE_LENGTH_LONG_SHORT(new Comparator<SortLine>() {
		@Override
		public int compare(SortLine o1, SortLine o2) {
			return o2.getTextForComparison().length() - o1.getTextForComparison().length();

		}
	}),
	HEXA(new Comparator<SortLine>() {   //TODO error handling?
		@Override
		public int compare(SortLine o1, SortLine o2) {
			return toHex(o1.getTextForComparison()).compareTo(toHex(o2.getTextForComparison()));
		}

		protected BigInteger toHex(String textForComparison) {
			if (textForComparison == null) {
				return BigInteger.ZERO;
			}
			textForComparison = textForComparison.trim().replaceAll("^0x", "");

			return new BigInteger(textForComparison, 16);
		}
	});

	private Comparator<SortLine> comparator;
	private ComparatorAdapterFactory factory;

	Sort(Comparator<SortLine> comparator) {
		this.comparator = comparator;
	}

	Sort(ComparatorAdapterFactory factory) {
		this.factory = factory;
	}

	public <T extends SortLine> List<T> sortLines(List<T> lines, SortSettings.BaseComparator baseComparator, String languageTag) {
		List<T> sortedLines = new ArrayList<T>(lines);
		switch (this) {
			case SHUFFLE:
				Collections.shuffle(sortedLines);
				break;
			case REVERSE:
				Collections.reverse(sortedLines);
				break;
			default:
				sortedLines.sort(getSortLineComparator(baseComparator, languageTag));
		}
		return sortedLines;
	}

	public Comparator<SortLine> getSortLineComparator(SortSettings.BaseComparator baseComparatorEnum, String languageTag) {
		Comparator<String> baseComparator = getStringComparator(baseComparatorEnum, languageTag);
		if (factory != null) {
			return factory.adapter(baseComparator);
		} else {
			return comparator;
		}
	}

	public static Comparator<String> getStringComparator(SortSettings.BaseComparator baseComparator, String languageTag) {
		Comparator comparator;
		switch (baseComparator) {
			case NORMAL:
				comparator = null;
				break;
			case NATURAL:
				comparator = new NaturalOrderComparator();
				break;
			case LOCALE_COLLATOR:
				try {
					Collator instance = Collator.getInstance(Locale.forLanguageTag(languageTag));
					String rules = ((RuleBasedCollator) instance).getRules();
					RuleBasedCollator correctedCollator = new RuleBasedCollator(rules.replaceAll("<'\u005f'", "<' '<'\u005f'"));
					comparator = correctedCollator;
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
				break;
			default:
				throw new RuntimeException("invalid enum");
		}

		return comparator;
	}

	static abstract class ComparatorAdapterFactory {
		abstract Comparator<SortLine> adapter(Comparator<String> comparator);
	}

}
