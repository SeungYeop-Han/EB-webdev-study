//////////////////////////////////////////////////
////////////////      진입점      ////////////////
//////////////////////////////////////////////////

let startButton = document.getElementById("startButton");
startButton.onclick = () => {

    document.getElementById("startButton").remove();

    let numSpan = Number(document.getElementById("numSpan").value);
    let numPlayer = Number(document.getElementById("numPlayer").value);
    let boardSize = Number(document.getElementById("boardSize").value);
    let game = new Game(numSpan, numPlayer, boardSize);

    // 플레이 섹션
    let playSection = document.getElementById("playSection");

    // 플레이 섹션 call 버튼 생성
    let callButton = document.createElement("button");
    callButton.innerHTML = "CALL";
    callButton.setAttribute("id", "callButton");
    callButton.style.backgroundColor = "red";
    callButton.style.color = "white";
    playSection.appendChild(callButton);

    callButton = document.getElementById("callButton");
    callButton.onclick =() => {
        game.nextTurn();
    };
    
    // 플레이어 수만큼 보드 생성
    for (let i = 0; i < numPlayer; i++) {

        // 보드 생성
        let playerBoard = document.createElement("table");
        playerBoard.setAttribute("id", (i+1) + "p");

        let row = document.createElement("tr");
        playerBoard.appendChild(row);

        let col;
        col = document.createElement("th");
        col.setAttribute("id", (i+1) + "pth")
        col.innerText = (i+1) + "p";
        row.appendChild(col);
        for (let j = 0; j < boardSize; j++) {
            col = document.createElement("td");
            col.setAttribute("id", (i+1) + "ptd" + (j+1))
            row.appendChild(col);
        }

        playSection.appendChild(playerBoard);
    }

    // Game Publisher에게 다음의 핸들러를 Subscriber로 등록
    game.add((value) => {
        let players = value.players;

        for (let i = 0; i < players.length; i++) {
            let p = players[i];
            let board = p.board;
            let checkBoard = p.checkBoard;
            let bingo = p.bingo;

            for (let j = 0; j < board.length; j++) {
                let cell = document.getElementById((i+1) + "ptd" + (j+1))
                cell.innerText = board[j];
                cell.style.backgroundColor = (checkBoard[j]) ? "red" : "white";
            }

            let th = document.getElementById((i+1) + "pth");
            if (bingo) {
                th.innerText = "BINGO!";
                callButton.disabled = true;
                callButton.style.backgroundColor = "grey";
                
                let resultMsg = document.createElement("h2");
                resultMsg.innerText = (i+1) + "p 승리!";
                playSection.appendChild(resultMsg);
            }
        }
    });
};


///////////////////////////////////////////////////////
////////////////      클래스 선언      ////////////////
//////////////////////////////////////////////////////

class Publisher {

    add(sub) {
        throw new Error("Publisher.add(sub) 메서드는 반드시 재정의되어야 합니다.");
    }

    remove(sub) {
        throw new Error("Publisher.remove(sub) 메서드는 반드시 재정의되어야 합니다.");
    }

    notifyAll() {
        throw new Error("Publisher.notifyAll() 메서드는 반드시 재정의되어야 합니다.");
    }
}


class Subscriber {

    update() {
        throw new Error("Subscriber.update 메서드는 반드시 재정의되어야 합니다.");
    }
}


class Game extends Publisher {

    static MIN_NUM_SPAN = 20;
    static MAX_NUM_SPAN = 30;
    static MIN_PLAYER = 2;
    static MAX_PLAYER = 5;
    static MIN_PLAYER_BOARD_SIZE = 3;
    static MAX_PLAYER_BOARD_SIZE = 10;

    constructor(numSpan, numPlayer, boardSize) {

        super();

        if (numSpan < Game.MIN_NUM_SPAN || Game.MAX_NUM_SPAN < numSpan) {
            throw new Error(`빙고 숫자 범위는 ${Game.MIN_NUM_SPAN} 이상 ${Game.MAX_NUM_SPAN} 이하여야 합니다.`);
        }

        if ( numPlayer < Game.MIN_PLAYER || Game.MAX_PLAYER < numPlayer ) {
            throw new Error(`플레이어 수는 ${Game.MIN_PLAYER}명 이상 ${Game.MAX_PLAYER}명 이하여야 합니다.`);
        }

        if ( boardSize < Game.MIN_PLAYER_BOARD_SIZE || Game.MAX_PLAYER_BOARD_SIZE < boardSize ) {
            throw new Error(`플레이어의 보드 크기는 ${Game.MIN_PLAYER_BOARD_SIZE} 이상 ${Game.MAX_PLAYER_BOARD_SIZE} 이하여야 합니다.`);
        }

        if (boardSize > numSpan) {
            throw new Error("플레이어의 보드의 크기가 빙고 숫자 범위보다 클 수 없습니다.");
        }

        // Publisher
        this.host = new Host(numSpan);

        // Host's subscribers
        for (let i = 0; i < numPlayer; i++) {
            this.host.add(new Player(`${i+1}p`, numSpan, boardSize));
        }
        
        // Game's subscribers
        this.renderers = [];
    }

    /**
     * @param rendererFunction - 변경된 값을 인자로 받아 DOM 객체에 반영하는 함수
     */
    add(rendererFunction) {
        if (rendererFunction instanceof Function) {
            this.renderers.push(rendererFunction);
        } else {
            throw new Error("ValuePublisher.add 메서드에는 DOM 렌더러 함수가 전달되어야 하며")
        }
    }

    remove(rendererFunction) {
        // ToDo
    }

    notifyAll() {
        for (let renderer of this.renderers) {
            renderer(this.value);
        }
    }

    nextTurn() {
        this.host.call();
        this.value = {
            thisTurn: this.host.curTurn,
            numCalled: this.host.numCalled,
            players: this.host.subscribers
        }
        this.notifyAll();
    }
}


class Host extends Publisher {

    constructor(numSpan) {

        super();

        // [1, 2, 3, ..., numSpan]
        this.numPool = Array.from({length: numSpan}, (val, idx) => idx + 1);

        // shuffle
        this.numPool.sort(() => Math.random() - 0.5);

        // 현재 턴
        this.curTurn = 0;

        // 현재 턴에 불린 값
        this.numCalled = 0;

        this.subscribers = [];
    }

    add(sub) {
        if (sub instanceof Subscriber) {
            this.subscribers.push(sub);
        } else {
            throw new Error("Publisher.add(sub)에 전달된 인자가 Observer가 아닙니다.");
        }
    }

    remove(sub) {
        // ToDo: 추후 구현
    }

    call() {
        this.numCalled = this.numPool[this.curTurn++];
        this.notifyAll();
    }

    notifyAll() {
        this.subscribers.forEach(sub => sub.update(this.numCalled))
    }
}


class Player extends Subscriber {

    constructor(name, numSpan, boardSize) {

        super();

        this.name = name;

        this.board = Array.from({length: numSpan}, (val, idx) => idx + 1);
        this.board.sort(() => Math.random() - 0.5);
        this.board = this.board.slice(0, boardSize);

        this.checkBoard = []
        this.checkBoard.length = boardSize;
        this.checkBoard = this.checkBoard.fill(false);

        this.bingo = false;
    }

    update(numCalled) {

        // 확인 용
        console.log("name = " + this.name);
        console.log("board = " + this.board.join(", "));
        console.log("check = " + this.checkBoard.join(", "));
        console.log("bingo = " + this.bingo);
        
        // check
        let loc = this.board.indexOf(numCalled);
        if (loc != -1) {
            this.checkBoard[loc] = true;
        }

        // isComplete?
        if (this.checkBoard.every(e => e)) {
            this.bingo = true;
        }
    }
}