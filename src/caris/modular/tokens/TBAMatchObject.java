package caris.modular.tokens;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TBAMatchObject implements Comparable<TBAMatchObject>{
	
	public enum MatchType {
		QUALIFICATIONS {
			@Override
			public String toString() {
				return "Qualifications";
			}
		},
		QUARTER_FINALS {
			@Override
			public String toString() {
				return "Quarter-Finals";
			}
		},
		SEMI_FINALS {
			@Override
			public String toString() {
				return "Semi-Finals";
			}
		},
		FINALS {
			@Override
			public String toString() {
				return "Finals";
			}
		},
	}
	
	@SuppressWarnings("serial")
	public final Map<String, MatchType> matchTypeKeys = new HashMap<String, MatchType>() {{
		put("qm", MatchType.QUALIFICATIONS);
		put("qf", MatchType.QUARTER_FINALS);
		put("sf", MatchType.SEMI_FINALS);
		put("f", MatchType.FINALS);
	}};
	
	public String eventKey;
	public int matchNumber;
	public MatchType matchType;
	public long predictedTime;
	private LocalDateTime predictedLDT;
	public String[] redAlliance;
	public String[] blueAlliance;
	
	public TBAMatchObject( String eventKey, int matchNumber, String matchType, long predictedTime, String[] redAlliance, String[] blueAlliance ) {
		this.eventKey = eventKey;
		this.matchNumber = matchNumber;
		this.matchType = matchTypeKeys.get(matchType);
		this.predictedTime = predictedTime;
		this.predictedLDT = LocalDateTime.ofEpochSecond(predictedTime, 0, ZoneOffset.UTC);
		this.redAlliance = redAlliance;
		this.blueAlliance = blueAlliance;
	}
	
	public String getDayOfWeek() {
		return DateTimeFormatter.ofPattern("EEEE").format(predictedLDT);
	}
	
	public String getDate() {
		return DateTimeFormatter.ofPattern("MM/dd/yyyy").format(predictedLDT);
	}
	
	public String getTime() {
		return DateTimeFormatter.ofPattern("hh:mm:ss").format(predictedLDT);
	}

	@Override
	public int compareTo(TBAMatchObject o) {
		if( matchType.compareTo(o.matchType) == 0 ) {
			return matchNumber - o.matchNumber;
		} else {
			return matchType.compareTo(o.matchType);
		}
	}
}