import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { classroomApi } from '@/api/classroom'

export const useClassroomStore = defineStore('classroom', () => {
  const currentClassroom = ref(null)
  const participants = ref([])
  const messages = ref([])
  const classroomStatus = ref('idle')

  const isLive = computed(() => currentClassroom.value?.status === 'LIVE')
  const participantCount = computed(() => participants.value.length)
  const classroomId = computed(() => currentClassroom.value?.id || null)

  async function loadClassroom(id) {
    classroomStatus.value = 'loading'
    try {
      const res = await classroomApi.getClassroomDetail(id)
      currentClassroom.value = res.data.classroom
      participants.value = res.data.participants || []
      classroomStatus.value = currentClassroom.value.status === 'LIVE' ? 'live' : 'idle'
      return res
    } catch (error) {
      classroomStatus.value = 'idle'
      throw error
    }
  }

  async function loadMessages(id) {
    try {
      const res = await classroomApi.getClassroomMessages(id, { pageNum: 1, pageSize: 200 })
      if (res.data) {
        messages.value = res.data.records || res.data
      }
    } catch (error) {
      console.error('加载消息失败:', error)
    }
  }

  async function endClassroom(id) {
    const res = await classroomApi.endClassroom(id)
    if (currentClassroom.value) {
      currentClassroom.value.status = 'ENDED'
    }
    classroomStatus.value = 'ended'
    return res
  }

  function addMessage(message) {
    messages.value.push(message)
  }

  function updateParticipants(list) {
    participants.value = list
  }

  function resetClassroom() {
    currentClassroom.value = null
    participants.value = []
    messages.value = []
    classroomStatus.value = 'idle'
  }

  return {
    currentClassroom,
    participants,
    messages,
    classroomStatus,
    isLive,
    participantCount,
    classroomId,
    loadClassroom,
    loadMessages,
    endClassroom,
    addMessage,
    updateParticipants,
    resetClassroom
  }
})
