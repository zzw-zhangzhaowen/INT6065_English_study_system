<template>
  <div class="ai-md" v-html="html"></div>
</template>

<script setup>
import { computed } from 'vue'
import { mdToHtml } from '@/ai/engine/text'

const props = defineProps({
  content: { type: String, default: '' }
})

const html = computed(() => mdToHtml(props.content || ''))
</script>

<style scoped>
.ai-md {
  word-break: break-word;
}
</style>
