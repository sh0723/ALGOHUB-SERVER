package com.gamzabat.algohub.feature.notification.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository{
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
	private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

	@Override
	public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
		emitters.put(emitterId, sseEmitter);
		return sseEmitter;
	}

	@Override
	public void saveEventCache(String eventCacheId, Object event) {
		eventCache.put(eventCacheId, event);
	}

	@Override
	public Map<String, SseEmitter> findAllEmitterStartWithByEmail(String email) {
		return emitters.entrySet().stream()
			.filter(entry -> entry.getKey().startsWith(email))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public Map<String, SseEmitter> findAllEmitterStartWithByEmailInList(List emails) {
		return null;
	}

	@Override
	public Map<String, Object> findAllEventCacheStartWithByEmail(String email) {
		return emitters.entrySet().stream()
			.filter(entry -> entry.getKey().startsWith(email))
			.collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
	}

	@Override
	public void deleteById(String id) {
		emitters.remove(id);
	}

	@Override
	public void deleteAllEmitterStartWithId(String email) {
		emitters.forEach((key,emitter) -> {
			if(key.startsWith(email)){
				emitters.remove(key);
			}
		});
	}

	@Override
	public void deleteAllEventCacheStartWithId(String email) {
		emitters.forEach((key,emitter)->{
			if(key.startsWith(email)){
				emitters.remove(key);
			}
		});
	}
}
