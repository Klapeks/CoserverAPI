import { Socket } from "net";

const config = {
    securityKey: "your security key here :)",
    ip: "localhost",
    port: 22565
}

const securitySplitor = "0x25G";
let bungeeDeltas: {
    time: number, // Just random numbers
    speed: number // Just random numbers
};
let meDelta = {
    time: Date.now()%1000000, // Just random numbers
    speed: 93 // Just random numbers
}
const bungee = {
    async sendSecureMsg(msg: string, response: (msg: string)=>void) {
        if (!bungeeDeltas) await bungee.getBungeePublicKey();
        let pstr = meDelta.time+"_"+meDelta.speed;
        pstr = Buffer.from(pstr).toString("base64");
        bungee.sendRawMessage("net/"+pstr+"/dec/"+bungee.NETing.encode(msg, bungeeDeltas.time, bungeeDeltas.speed), (e) => {
            response(bungee.NETing.decode(e, meDelta.time, meDelta.speed));
        });
    },
    getBungeePublicKey(): Promise<void> {
        return new Promise<void>(resolve => {
            bungee.sendRawMessage(config.securityKey + securitySplitor + "givemenetpswpls", (msg) => {
                msg = Buffer.from(msg, 'base64').toString();
                bungeeDeltas = {
                    time: parseInt(msg.split("_")[0]),
                    speed: parseInt(msg.split("_")[1])
                };
                resolve();
            });
        });
    },
    sendRawMessage(msg: string, resp: (msg: string)=>void) {
        let client = new Socket();
        client.on('data', (data) => {
            resp(data.slice(2).toString());
            client.destroy();
        });
        // client.on("close", () => {});
        client.connect(config.port, config.ip, () => {
            client.write(new Uint8Array([Math.floor(msg.length/256), (msg.length%256)]));
            client.write(msg, "utf-8");
        });
    },
    NETing: {
        encode(str: string, deltaTime: number, deltaSpeed: number): string {
            const randomAcc = "ghijklmnopqrstuvwxyz";
            let arr = Buffer.from(str).toString("base64");
            let result = "av";
            for (let i = bungee.NETing.sum(deltaTime); i>0;i--) {
                // result += Character.toString(randomAcc.charAt((int)(rand.nextInt(randomAcc.length()))));
                result += randomAcc.at(Math.floor(Math.random()*randomAcc.length));
            }
            for (let i = 0; i < arr.length; i++) {
                let e = (arr.charCodeAt(i) + deltaSpeed).toString(16);
                if (e.length>3) return str;
                while(e.length<3) e = randomAcc.at(Math.floor(Math.random()*randomAcc.length))+e;
                result += e;
            }
            return result;
        },
        decode(str: string, deltaTime: number, deltaSpeed: number): string {
            if (!str.startsWith("av")) throw "DECODE ERROR";
            const randomAcc = "ghijklmnopqrstuvwxyz";
            str = str.substring(2).substring(bungee.NETing.sum(deltaTime));
            let r = "";
            for (let i = 0; i < str.length; i+=3) {
                let e = str.substring(i, i+3);
                while (randomAcc.includes(e.at(0)+"")) {
                    e = e.substring(1);
                }
                r += String.fromCharCode(parseInt(e, 16)-deltaSpeed);
            }
            return Buffer.from(r, 'base64').toString();
        },
        sum(s: number): number {
            let e = 0;
            while (true) {
                e += s%10;
                s = Math.floor(s/10);
                if (s < 10) {
                    e+=s;
                    break;
                } 
            }
            return e;
        }
    }
};
// bungee.sendRawMessage("hello world", (msg) => {
//     console.log("Hello data: " + msg)
// });
bungee.sendSecureMsg("some random request", (msg) => {
    console.log("SecureNet: " + msg);
});
export {bungee};
