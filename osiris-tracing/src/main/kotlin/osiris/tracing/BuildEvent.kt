package osiris.tracing

public typealias BuildStart = () -> Event.Start.Creator

public typealias BuildEnd<T> = (T) -> Event.End.Creator
