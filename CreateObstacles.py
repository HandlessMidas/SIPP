import random
from Map import Map


def save_obstacles(trajs, path):
    with open(path, 'w') as f:
        obsts = []
        for traj in trajs:
            strs = ["obst " + str(len(traj))]
            ii, jj = zip(*traj)
            strs += [" ".join(map(str, ii)), " ".join(map(str, jj))]
            obsts.append("\n".join(strs))
        f.write("\n".join(obsts))


def load_obstacles(path):
    trajs = []
    with open(path, 'r') as f:
        lines = f.readlines()
        ii = None
        jj = None
        for l in lines:
            if l.startswith("obst"):
                trajs.append([])
                ii = None
                jj = None
            elif ii is None:
                ii = list(map(int, l.split(' ')))
            else:
                jj = list(map(int, l.split(' ')))
                trajs[-1] = list(zip(ii, jj))
    return trajs


def random_cycle_traj(w, h, upT, mv=None, s=(None, None)):

    '''
    Зацикленная траектория длины не больше upT
    В mv можно передать распределение на переходы (как в moves)

    w, h - размеры карты

    returns: [(i: int, j: int)]
    '''

    si, sj = s
    moves = [(0, 0)] + [(0, 1), (0, -1), (1, 0), (-1, 0)]*2
    if mv is not None:
        moves = mv

    if si is None or sj is None:
        si = random.randint(0, h-1)
        sj = random.randint(0, w-1)

    ci, cj = si, sj

    traj = [(ci, cj)]

    for _ in range(upT // 2):
        mi, mj = random.choice(moves)
        if 0 <= ci + mi < h:
            ci = ci + mi
        if 0 <= cj + mj < w:
            cj = cj + mj
        traj.append((ci, cj))

    traj += bfs(ci, cj, si, sj, w, h)

    return traj


def random_linear_traj(w, h, upT, mv=None, s=(None, None)):

    '''
    Траектория без обязательного зацикливания # <= upT

    returns: [(i: int, j: int)]
    '''

    si, sj = s
    moves = [(0, 0)] + [(0, 1), (0, -1), (1, 0), (-1, 0)]*2
    if mv is not None:
        moves = mv

    if si is None or sj is None:
        si = random.randint(0, h-1)
        sj = random.randint(0, w-1)

    ci, cj = si, sj

    traj = [(ci, cj)]

    for _ in range(upT):
        mi, mj = random.choice(moves)
        if not (0 <= ci + mi < h):
            mi *= -1
        ci = ci + mi
        if not (0 <= cj + mj < w):
            mj *= -1
        cj = cj + mj
        traj.append((ci, cj))

    return traj


def random_uncollide_linear_traj(m, w, h, upT, mv=None, s=(None, None)):

    '''
    Траектория без коллизий со статическими препятствиями

    m: Map - карта, на которой ищем траекторию

    returns: [(i: int, j: int)]
    '''

    si, sj = s
    moves = [(0, 0)] + [(0, 1), (0, -1), (1, 0), (-1, 0)]*3
    if mv is not None:
        moves = mv

    if si is None or sj is None:
        si = random.randint(0, h-1)
        sj = random.randint(0, w-1)
        
        while not m.Traversable(si, sj):
            si = random.randint(0, h-1)
            sj = random.randint(0, w-1)

    ci, cj = si, sj

    traj = [(ci, cj)]

    k = 0
    l = 0
    stop = 500
    while k < upT:
        l += 1
        mi, mj = random.choice(moves)
        ni, nj = ci, cj
        if 0 <= ci + mi < h:
            ni = ci + mi
        if 0 <= cj + mj < w:
            nj = cj + mj
        if m.Traversable(ni, nj):
            ci, cj = ni, nj
            k += 1
            traj.append((ci, cj))
        if l > stop:
            break

    return traj

def bfs(i1, j1, i2, j2, w, h):
    moves = [(0, 1), (0, -1), (1, 0), (-1, 0)]
    parent = {(i1, j1): (None, None)}

    q = [(i1, j1)]
    while True:
        ci, cj = q.pop(0)
        if ci < 0 or ci >= h or cj < 0 or cj >= h:
            continue

        if ci == i2 and cj == j2:
            break

        for mi, mj in moves:
            new = (ci + mi, cj + mj)
            if new in parent:
                continue
            q.append(new)
            parent[new] = (ci, cj)

    b = (i2, j2)
    backtrace = [b]

    while True:
        if b == (i1, j1):
            backtrace.append(b)
            break
        b = parent[b]

    return list(reversed(backtrace))


def bfsMap(m, i1, j1, i2, j2, w, h):
    moves = [(0, 1), (0, -1), (1, 0), (-1, 0)]
    parent = {(i1, j1): (None, None)}

    q = [(i1, j1)]
    while True:
        ci, cj = q.pop(0)
        if ci < 0 or ci >= h or cj < 0 or cj >= h:
            continue

        if ci == i2 and cj == j2:
            break

        for mi, mj in moves:
            new = (ci + mi, cj + mj)
            if new in parent or not m.Traversable(*new):
                continue
            q.append(new)
            parent[new] = (ci, cj)

    b = (i2, j2)
    backtrace = [b]

    while True:
        if b == (i1, j1):
            backtrace.append(b)
            break
        b = parent[b]

    return list(reversed(backtrace))


if __name__ == '__main__':
    trajs = [random_linear_traj(30, 30, 10) for _ in range(10)]
    save_obstacles(trajs, 'test.txt')
    # trajs = load_obstacles('test.txt')
    # print(len(trajs))
    # print(*trajs, sep='\n')
