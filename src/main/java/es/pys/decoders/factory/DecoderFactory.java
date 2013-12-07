package es.pys.decoders.factory;

import es.pys.decoders.FilmaffinityDecoder;
import es.pys.decoders.ZinemaDecoder;

public class DecoderFactory {

	public static Decoder getDecoder(DecoderType tipo) {
		Decoder decoder = null;
		switch (tipo) {
		case FILMAFFINITY:
			decoder = new FilmaffinityDecoder();
			break;

		case ZINEMA:
			decoder = new ZinemaDecoder();
			break;

		default:
			// throw some exception
			break;
		}

		return decoder;
	}

	public static Decoder getDecoder(String url) {
		if (url.startsWith("http://www.zinema.com")) {
			return DecoderFactory.getDecoder(DecoderType.ZINEMA);
		} else if (url.startsWith("http://www.filmaffinity.com")) {
			return DecoderFactory.getDecoder(DecoderType.FILMAFFINITY);
		}

		return null;
	}
}
