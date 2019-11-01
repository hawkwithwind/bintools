<template>
    <div>
        <div class="input">
            <el-input class="text" type="textarea" :rows="3" placeholder="等待输入 protobuf 二进制数据" v-model="inputProtoBin"></el-input>

            <div class="action">
                <el-button class="button" type="primary" size="medium" @click="decode">解码</el-button>
                <el-select class="select" v-model="selectProtoClass" size="medium" placeholder="可选择proto class" filterable clearable>
                    <el-option v-for="item in protoInfo.classList" :key="item" :label="item" :value="item"></el-option>
                </el-select>

                <div class="space"></div>

                <el-dropdown @command="didClickHistory">
                    <span class="el-dropdown-link">
                        历史记录<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item :command="index" v-for="(item, index) in histories" v-bind:key="item.protoBin.substring(0, 10)">
                            {{ (item.protoClass && item.protoClass + ' - ' || '') + item.protoBin.substring(0, 10) + '...' + item.protoBin.substring(item.protoBin.length-10, item.protoBin.length)}}
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>

        <div class="output">
            <vue-json-pretty class="json-pretty" :class="{two: !!decodedClassJSON}" v-if="decodedRawJSON" :data="decodedRawJSON"></vue-json-pretty>
            <vue-json-pretty class="json-pretty" :class="{two: !!decodedClassJSON}" v-if="decodedClassJSON" :data="decodedClassJSON"></vue-json-pretty>
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
            inputProtoBin: "",
            protoInfo: {
                classList: [],
                getClassFunc: null
            },
            selectProtoClass: null,

            decodedRawJSON: null,
            decodedClassJSON: null,

            /**
             * {
             *     protoClass: '',
             *     protoBin: ''
             * }
             */
            histories:[]
        }
    },
    components: {
        VueJsonPretty
    },
    async mounted() {
        await this._parseProtoIfNeed();
    },
    methods: {
        async decode() {
            if (!this.inputProtoBin) {
                this.$message.warning('请输入 protobuf 数据');
                return;
            }

            await this._parseProtoIfNeed();

            let success = true;

            const buffer = Buffer.from(this.inputProtoBin, "hex");
            this.decodedRawJSON = getData(buffer);

            if (this.selectProtoClass) {
                const ProtoClass = this.protoInfo.getClassFunc(this.selectProtoClass);
                try {
                    const inBuffer = Buffer.from(this.inputProtoBin, "hex");
                    this.decodedClassJSON = ProtoClass.decode(inBuffer);
                } catch (e) {
                    this.$message.error(`${this.selectProtoClass} 解码失败：\n${e.toString()}`);
                    console.error(e);
                    success = false;
                }
            } else {
                this.decodedClassJSON = null;
            }

            if (success) {
                this._appendHistory(this.inputProtoBin, this.selectProtoClass);
            }
        },
        didClickHistory(index) {
            const selectedHis = this.histories[index];
            this.inputProtoBin = selectedHis.protoBin;
            this.selectProtoClass = selectedHis.protoClass;

            this.decode();
        },

        async _parseProtoIfNeed() {
            if (this.protoInfo.getClassFunc) {
                return this.protoInfo;
            }

            let protoFileText = await downloadStaticFile("ALL.proto");

            const root = protobufjs.parse(protoFileText, { keepCase: true }).root;
            const pkg = root.lookup('com.wechat');

            this.protoInfo = {
                "classList" : Object.keys(pkg.nested).sort(),
                "getClassFunc": (className) => {
                    return pkg.lookup(className);
                }
            };

            return this.protoInfo;
        },

        _appendHistory(protoBin, protoClass) {
            const index = this.histories.findIndex((history) => {
                return history.protoBin === protoBin;
            });

            if (index !== -1) {
                this.histories.splice(index, 1);
            }

            this.histories.unshift({
                protoClass: protoClass,
                protoBin: protoBin
            })
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
        align-items: center;

        .button {
            margin-right: 20px;
        }

        .select {
            min-width: 300px;
        }

        .space {
            flex-grow: 1;
        }

        .el-dropdown-link {
            cursor: pointer;
            color: #409EFF;
        }
        .el-icon-arrow-down {
            font-size: 12px;
        }
    }
}

.output {
    display: flex;
    flex-direction: row;
    margin-top: 20px;

    .json-pretty {
        overflow-x: scroll;

        padding: 10px;
        border: 1px solid #e4e4e4;
        border-radius: 4px;
        background-color: #FCFCFC;
        white-space: nowrap;

        &.two {
            width: calc(50% - 5px);
            &:first-child {
                margin-right: 10px;
            }
        }
    }
}

</style>
