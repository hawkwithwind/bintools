<template>
    <div>
        <div class="input">
            <textarea class="text pure-u-1" v-model="inputText" placeholder="等待输入" rows="5"></textarea>
            <div class="action">
                <button class="pure-button pure-button-primary" @click="inspect">Inspect</button>

                <div class="decode-section">
                    <v-select class="select" v-model="selectProtoClass" placeholder="请选择 proto class" :options="protoInfo.classList" />
                    <button class="pure-button pure-button-primary" @click="decode">解码</button>
                </div>
            </div>
        </div>

        <div class="output">
            <vue-json-pretty class="json-pretty" v-if="decodedProtoJSON" :data="decodedProtoJSON"></vue-json-pretty>
        </div>
    </div>
</template>

<script>

import protobufjs from "protobufjs";
import {downloadStaticFile} from "./utils";
import VueJsonPretty from 'vue-json-pretty';
import { getData, getProto } from './rawproto'

export default {
    name: 'ProtoBin',
    data() {
        return {
            inputText: "",
            protoInfo: {
                classList: [],
                getClassFunc: null
            },
            selectProtoClass: null,
            decodedProtoJSON: null,
        }
    },
    components: {
        VueJsonPretty
    },
    async mounted() {
        await this._parseProtoIfNeed();



        console.log(this.protoInfo);
    },
    methods: {
        async inspect() {
            if (!this.inputText) {
                this.$message.warning('请输入 protobuf 数据');
                return;
            }

            const buffer = Buffer.from(this.inputText, "hex");
            this.decodedProtoJSON = getData(buffer);
        },

        async decode() {
            if (!this.inputText) {
                this.$message.warning('请输入 protobuf 数据');
                return;
            }

            if (!this.selectProtoClass) {
                this.$message.warning('请选择 proto class');
                return;
            }

            const ProtoClass = this.protoInfo.getClassFunc(this.selectProtoClass);
            try {
                const inBuffer = Buffer.from(this.inputText, "hex");
                this.decodedProtoJSON = ProtoClass.decode(inBuffer);
            } catch (e) {
                this.$message.error(`解码失败：\n${e.toString()}`);
                console.error(e);
            }
        },

        async _parseProtoIfNeed() {
            if (this.protoInfo.getClassFunc) {
                return this.protoInfo;
            }

            let protoFileText = await downloadStaticFile("ALL.proto");

            const root = protobufjs.parse(protoFileText, { keepCase: true }).root;
            const pkg = root.lookup('com.wechat');

            this.protoInfo = {
                "classList" : Object.keys(pkg.nested),
                "getClassFunc": (className) => {
                    return pkg.lookup(className);
                }
            };

            return this.protoInfo;
        }
    }
}
</script>

<style scoped lang="scss">
.input {
    .text {
        font-size: 12px;
    }

    .action {
        margin-top: 10px;

        display: flex;
        flex-direction: row;

        .pure-button {
            margin-right: 10px;
        }

        .decode-section {
            display: flex;
            flex-direction: row;
            align-items: center;

            margin-left: 100px;

            .select {
                min-width: 300px;
            }

            .pure-button {
                margin-left: 10px;
            }
        }
    }
}

.output {
    margin-top: 20px;

    .json-pretty {
        padding: 10px;
        border: 1px solid #e4e4e4;
        border-radius: 4px;
        background-color: #FCFCFC;
        white-space: nowrap;
    }
}

</style>
