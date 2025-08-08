package osiris.tracing

public typealias BuildStart = () -> Event.Start.Creator

public typealias BuildEnd<T> = (result: T) -> Event.End.Creator
